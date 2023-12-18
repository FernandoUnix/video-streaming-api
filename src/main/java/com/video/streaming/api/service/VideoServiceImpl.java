package com.video.streaming.api.service;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.exception.StorageException;
import com.video.streaming.api.repository.FileMetadataRepository;
import com.video.streaming.api.request.FileMetadataRequest;
import com.video.streaming.api.util.CustomMultipartFile;
import com.video.streaming.api.util.Range;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private MinioStorageService storageService;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    Map<String, byte[]> chunkDataMap = new HashMap<>();

    @Override
    @Transactional
    public UUID save(FileMetadataRequest fileMetadataRequest) {

            FileMetadata metadata = fileMetadataRequest.getId() == null ? new FileMetadata(UUID.randomUUID()) : fileMetadataRepository.findByIdAndIsDeletedFalse(fileMetadataRequest.getId()).get();

            metadata.setTitle(fileMetadataRequest.getTitle());
            metadata.setCast(fileMetadataRequest.getCast());
            metadata.setSynopsis(fileMetadataRequest.getSynopsis());
            metadata.setDirector(fileMetadataRequest.getDirector());
            metadata.setYearOfRelease(fileMetadataRequest.getYearOfRelease());
            metadata.setGenre(fileMetadataRequest.getGenre());

            FileMetadata fileMetaDataSaved = fileMetadataRepository.save(metadata);

            return fileMetaDataSaved.getId();

    }

    @Override
    @Transactional
    public FileMetadata getMetadata(UUID id) {
       return fileMetadataRepository.findByIdAndIsDeletedFalse(id).get();
    }

    @Override
    @Transactional
    public void saveVideo(UUID id, String contentType, MultipartFile multipartFile) {

        try {

            FileMetadata metadata = fileMetadataRepository.findByIdAndIsDeletedFalse(id).get();
            metadata.setRunningTime(getVideoDurationInSeconds(multipartFile.getBytes()));
            metadata.setHttpContentType(contentType);
            metadata.setSize(multipartFile.getSize());

            fileMetadataRepository.save(metadata);
            storageService.save(multipartFile, id);

        } catch (Exception ex) {
            log.error("Exception occurred when trying to save the video", ex);
            throw new StorageException(ex);
        }
    }

    @Override
    @Transactional
    public void deleteVideo(UUID id) {
        try {
            FileMetadata metadata = fileMetadataRepository.findByIdAndIsDeletedFalse(id).get();
            metadata.setDeleted(true); // Soft Delete
        } catch (Exception ex) {
            log.error("Exception occurred when trying to delete the file", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasVideo(UUID uuid) {
        try {
            return storageService.get(uuid) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public ChunkWithMetadata fetchChunk(UUID uuid, Range range) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndIsDeletedFalse(uuid).orElseThrow();
        return new ChunkWithMetadata(fileMetadata, readChunk(uuid, range, fileMetadata.getSize()));
    }

    public MultipartFile handleFileUpload(MultipartFile file, int chunkNumber, int totalChunks) throws IOException {

        try {

            String fileName = file.getOriginalFilename();
            byte[] chunkBytes = file.getBytes();

            // Store the chunk in memory
            chunkDataMap.putIfAbsent(fileName, new byte[totalChunks * chunkBytes.length]);
            System.arraycopy(chunkBytes, 0, chunkDataMap.get(fileName), (chunkNumber - 1) * chunkBytes.length, chunkBytes.length);

            // Check if it's the last chunk and assemble the file
            if (chunkNumber == totalChunks) {
                byte[] assembledBytes = chunkDataMap.get(fileName);
                chunkDataMap.remove(fileName);
                return new CustomMultipartFile(assembledBytes);
            }

            return null;
        } catch (IOException e) {
            throw e;
        }
    }

    private byte[] readChunk(UUID uuid, Range range, long fileSize) {
        long startPosition = range.getRangeStart();
        long endPosition = range.getRangeEnd(fileSize);
        int chunkSize = (int) (endPosition - startPosition + 1);
        try(InputStream inputStream = storageService.getInputStream(uuid, startPosition, chunkSize)) {
            return inputStream.readAllBytes();
        } catch (Exception exception) {
            log.error("Exception occurred when trying to read file with ID = {}", uuid);
            throw new StorageException(exception);
        }
    }

    // Make sure you have FFmpeg installed on your system and accessible via the system path.
    private String getFfmpegCommand(File videoFile) {
        if (SystemUtils.IS_OS_WINDOWS) {
            // Windows
            return "ffmpeg.exe -i \"" + videoFile.getAbsolutePath() + "\" 2>&1 | findstr Duration";
        } else {
            // Linux
            return "ffmpeg -i \"" + videoFile.getAbsolutePath() + "\" 2>&1 | grep Duration";
        }
    }

    private Integer getVideoDurationInSeconds(byte[] file) throws IOException {

        File tempDir = Files.createTempDirectory(null).toFile();

        File tempFile = new File(tempDir, "temp.mp4");

        try {
            FileCopyUtils.copy(file, tempFile);
            String command = getFfmpegCommand(tempFile);

            Process process = new ProcessBuilder().command("bash", "-c", command).start();

            try (InputStream is = process.getInputStream(); Scanner scanner = new Scanner(is)) {
                String output = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

                String[] timeComponents = output.replace(" Duration: ", "").split(":");
                int hours = Integer.parseInt(timeComponents[0].trim());
                int minutes = Integer.parseInt(timeComponents[1].trim());
                double seconds = Double.parseDouble(timeComponents[2].substring(0, 2));// only get seconds and not milliseconds

                int totalSeconds = (int) (hours * 3600 + minutes * 60 + seconds);

                return totalSeconds;
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Exception occurred when trying to get video duration in seconds");
            return null;
        } finally {
            tempFile.delete();
            tempDir.delete();
        }
    }

    public record ChunkWithMetadata(
            FileMetadata metadata,
            byte[] chunk
    ) {}
}