package com.video.streaming.api.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ACCEPT_RANGES;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_RANGE;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.enums.Genre;
import com.video.streaming.api.request.FileMetadataRequest;
import com.video.streaming.api.response.FileMetadataResponse;
import com.video.streaming.api.response.PaginateFileMetadataResponse;
import com.video.streaming.api.service.EngagementStatisticService;
import com.video.streaming.api.service.FileMetadataService;
import com.video.streaming.api.service.VideoService;
import com.video.streaming.api.service.VideoServiceImpl;
import com.video.streaming.api.util.Range;
import com.video.streaming.api.util.Time;
import com.video.streaming.api.util.Url;
import com.video.streaming.api.util.UserMock;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private FileMetadataService FileMetadataService;

    @Autowired
    private EngagementStatisticService engagementStatisticService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HttpServletRequest request;

    @Value("${app.streaming.default-chunk-size}")
    public Integer defaultChunkSize;

    private static final String PLAY_VIDEO_URL = "/v1/videos/play/";

    @PostMapping
    public ResponseEntity<UUID> save(@RequestBody FileMetadataRequest model) {
        UUID fileUuid = videoService.save(model);
        return new ResponseEntity<>(fileUuid, HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UUID> update(@PathVariable UUID uuid, @RequestBody FileMetadataRequest model) {

        model.setId(uuid);
        return ResponseEntity.ok(videoService.save(model));
    }

    @PostMapping("/upload-file")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") UUID id,
            @RequestParam("type") String type,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks) {

        try {

           MultipartFile multipartFile = videoService.handleFileUpload(file, chunkNumber, totalChunks);

            if (multipartFile != null) { // upload completed
                videoService.saveVideo(id, type, multipartFile);
            }

          return new ResponseEntity<>("Chunk uploaded successfully", HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("Error uploading chunk", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteVideo(@PathVariable UUID uuid) {

        try {
            videoService.deleteVideo(uuid);
            return new ResponseEntity<>("Video deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting Video", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/play/{uuid}")
    public ResponseEntity<byte[]> readChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable UUID uuid) {

        try {

            Range parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
            VideoServiceImpl.ChunkWithMetadata chunkWithMetadata = videoService.fetchChunk(uuid, parsedRange);

            if (range == null) { //TODO, we need to add Logic to add views, now we are just checking if it's the beginning of the video
                engagementStatisticService.addViews(UserMock.USER_LOGGED_ID, uuid);
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(CONTENT_TYPE, chunkWithMetadata.metadata().getHttpContentType())
                    .header(ACCEPT_RANGES, "bytes")
                    .header(CONTENT_LENGTH, parsedRange.calculateContentLengthHeader(chunkWithMetadata.metadata().getSize()))
                    .header(CONTENT_RANGE, parsedRange.constructContentRangeHeader(chunkWithMetadata.metadata().getSize()))
                    .body(chunkWithMetadata.chunk());

        } catch (Exception e) {
            return new ResponseEntity<>("Sorry, Error to read Video".getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FileMetadataResponse> getVideoMetadata(@PathVariable UUID uuid) {

        try {

            FileMetadata metadata = videoService.getMetadata(uuid);
            engagementStatisticService.addImpressions(UserMock.USER_LOGGED_ID, uuid);

            return new ResponseEntity<>(getFileMetadataResponse(metadata), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<PaginateFileMetadataResponse> getVideos(
            @RequestParam(required = false) Integer yearOfRelease,
            @RequestParam(required = false) List<Genre> genre,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        boolean hasFilter = yearOfRelease != null || director != null ||  title != null || genre != null;

        Page<FileMetadata> fileMetadata = hasFilter ?
        FileMetadataService.findByFilters(yearOfRelease, genre, director, title, PageRequest.of(page, size), mongoTemplate) :
        FileMetadataService.findByIsDeletedFalseOrderByCreateAtDesc(PageRequest.of(page, size));

        PaginateFileMetadataResponse response = new PaginateFileMetadataResponse();
        response.setActualPage(fileMetadata.getNumber());
        response.setTotalPage(fileMetadata.getTotalPages());
        response.setTotalItems(fileMetadata.getTotalElements());

        List<FileMetadataResponse> filesMetadataResponse = fileMetadata
                .getContent()
                .stream()
                .map(metadata -> getFileMetadataResponse(metadata))
                .toList();

        response.setVideos(filesMetadataResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private FileMetadataResponse getFileMetadataResponse( FileMetadata metadata) {

        FileMetadataResponse fileMetadataResponse = new FileMetadataResponse();

        fileMetadataResponse.setId(metadata.getId());
        fileMetadataResponse.setCast(metadata.getCast());
        fileMetadataResponse.setDirector(metadata.getDirector());
        fileMetadataResponse.setSynopsis(metadata.getSynopsis());
        fileMetadataResponse.setYearOfRelease(metadata.getYearOfRelease());
        fileMetadataResponse.setRunningTime(Time.getRunningTime(metadata.getRunningTime()));
        fileMetadataResponse.setGenre(metadata.getGenre());
        fileMetadataResponse.setTitle(metadata.getTitle());
        fileMetadataResponse.setHttpContentType(metadata.getHttpContentType());
        fileMetadataResponse.setSize(metadata.getSize());

        boolean hasVideo = videoService.hasVideo(metadata.getId());
        fileMetadataResponse.setHasVideo(hasVideo);

        if (hasVideo) {
            fileMetadataResponse.setVideoUrl(Url.getBaseUrl(request) + PLAY_VIDEO_URL + metadata.getId());
        }

        return fileMetadataResponse;
    }
}