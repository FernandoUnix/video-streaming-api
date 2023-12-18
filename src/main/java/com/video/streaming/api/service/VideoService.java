package com.video.streaming.api.service;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.request.FileMetadataRequest;
import com.video.streaming.api.util.Range;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface VideoService {

    UUID save(FileMetadataRequest request) ;

    VideoServiceImpl.ChunkWithMetadata fetchChunk(UUID uuid, Range range);
    MultipartFile handleFileUpload(MultipartFile file, int chunkNumber, int totalChunks) throws IOException;
    void saveVideo(UUID id, String type, MultipartFile file);

    boolean hasVideo(UUID id);
    void deleteVideo(UUID id) throws Exception;
    FileMetadata getMetadata(UUID id);
}
