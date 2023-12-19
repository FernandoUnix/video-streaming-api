package com.video.streaming.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.enums.Genre;
import com.video.streaming.api.exception.StorageException;
import com.video.streaming.api.repository.FileMetadataRepository;
import com.video.streaming.api.request.FileMetadataRequest;
import com.video.streaming.api.util.FileMetaDataUtilTest;
import com.video.streaming.api.util.Range;
import io.minio.GetObjectResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VideoServiceImplTest {

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @Mock
    private MinioStorageService storageService;

    @InjectMocks
    private VideoServiceImpl videoServiceImpl;

    @Test
    public void testSaveMetaData() {

        FileMetadataRequest fileMetadataRequest = FileMetaDataUtilTest.getFileMetadataRequest();

        when(fileMetadataRepository.save(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());

        UUID id = videoServiceImpl.save(fileMetadataRequest);

        verify(fileMetadataRepository, times(1)).save(any());
        verify(fileMetadataRepository, times(0)).findByIdAndIsDeletedFalse(any());

        assertThat(id).isEqualTo(FileMetaDataUtilTest.metadataId);
    }

    @Test
    public void testUpdateMetaData() {

        FileMetadataRequest fileMetadataRequest = FileMetaDataUtilTest.getFileMetadataRequest();
        fileMetadataRequest.setId(FileMetaDataUtilTest.metadataId);

        when(fileMetadataRepository.save(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());
        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));

        UUID id = videoServiceImpl.save(fileMetadataRequest);

        verify(fileMetadataRepository, times(1)).save(any());
        verify(fileMetadataRepository, times(1)).findByIdAndIsDeletedFalse(any());

        assertThat(id).isEqualTo(FileMetaDataUtilTest.metadataId);
    }

    @Test
    public void testGetMetaData() {

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));

        FileMetadata fileMetaData = videoServiceImpl.getMetadata(FileMetaDataUtilTest.metadataId);

        verify(fileMetadataRepository, times(1)).findByIdAndIsDeletedFalse(any());

        assertThat(fileMetaData.getTitle()).isEqualTo("Spider-Man");
        assertThat(fileMetaData.getSynopsis()).isEqualTo(FileMetaDataUtilTest.SYNOPSIS);
        assertThat(fileMetaData.getDirector()).isEqualTo("Sam Raimi");

        Map<String, String> cast = new HashMap<>();
        cast.put("Peter Parker/Spider-Man", "Tobey Maguire");
        cast.put("Norman Osborn/Green Goblin", "Willem Dafoe");

        assertThat(fileMetaData.getCast()).isEqualTo(cast);
        assertThat(fileMetaData.getYearOfRelease()).isEqualTo(2002);
        assertThat(fileMetaData.getGenre()).isEqualTo(List.of(Genre.ACTION, Genre.ADVENTURE));
    }

    @Test
    public void testSaveVideo() throws Exception {

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));
        when(fileMetadataRepository.save(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());

        videoServiceImpl.saveVideo(FileMetaDataUtilTest.metadataId, "video/mp4" , FileMetaDataUtilTest.getMultiPartFile());

        verify(fileMetadataRepository, times(1)).findByIdAndIsDeletedFalse(any());
        verify(fileMetadataRepository, times(1)).save(any());
        verify(storageService, times(1)).save(any(), any());
    }

    @Test
    public void testSaveVideoThrowsException() throws Exception {

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));
        when(fileMetadataRepository.save(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());
        Mockito.doThrow(new StorageException(new RuntimeException("Exception occurred when trying to save the file"))).when(storageService).save(any(), any());

        assertThatThrownBy(() -> videoServiceImpl.saveVideo(FileMetaDataUtilTest.metadataId, "video/mp4" , FileMetaDataUtilTest.getMultiPartFile())).isInstanceOf(StorageException.class)
                .hasMessageContaining("Exception occurred when trying to save the file");
    }

    @Test
    public void testDeleteVideo(){

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));
        when(fileMetadataRepository.save(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());

        videoServiceImpl.deleteVideo(FileMetaDataUtilTest.metadataId);

        verify(fileMetadataRepository, times(1)).findByIdAndIsDeletedFalse(any());
        verify(fileMetadataRepository, times(1)).save(any());
    }

    @Test
    public void testDeleteVideoThrowsException(){

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(FileMetaDataUtilTest.getFileMetadata()));
        when(fileMetadataRepository.save(any())).thenThrow(new RuntimeException("Exception occurred when trying to delete the file"));

        assertThatThrownBy(() -> videoServiceImpl.deleteVideo(FileMetaDataUtilTest.metadataId)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exception occurred when trying to delete the file");
    }

    @Test
    public void testHasVideoFalse() throws Exception {

        when(storageService.get(FileMetaDataUtilTest.metadataId)).thenReturn(null);

        boolean hasVideo = videoServiceImpl.hasVideo(FileMetaDataUtilTest.metadataId);
        assertThat(hasVideo).isFalse();

        verify(storageService, times(1)).get(any());
    }

    @Test
    public void testHasVideoTrue() throws Exception {

        when(storageService.get(FileMetaDataUtilTest.metadataId)).thenReturn(new GetObjectResponse(null, "" , "", "", null));

        boolean hasVideo = videoServiceImpl.hasVideo(FileMetaDataUtilTest.metadataId);
        assertThat(hasVideo).isTrue();

        verify(storageService, times(1)).get(any());
    }

    @Test
    public void testHasVideoTrueThrowsException() throws Exception {

        when(storageService.get(FileMetaDataUtilTest.metadataId)).thenThrow(new RuntimeException());

        boolean hasVideo = videoServiceImpl.hasVideo(FileMetaDataUtilTest.metadataId);
        assertThat(hasVideo).isFalse();

        verify(storageService, times(1)).get(any());
    }

    @Test
    public void testFetchChunk() throws Exception {

        FileMetadata fileMetaData = FileMetaDataUtilTest.getFileMetadata();
        fileMetaData.setSize(123456L);
        fileMetaData.setHttpContentType("video/mp4");

        when(fileMetadataRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(fileMetaData));
        when(storageService.getInputStream(FileMetaDataUtilTest.metadataId, 1, 32)).thenReturn(FileMetaDataUtilTest.getMultiPartFile().getInputStream());

        VideoServiceImpl.ChunkWithMetadata chunkWithMetadata = videoServiceImpl.fetchChunk(FileMetaDataUtilTest.metadataId, new Range(1L,32L));

        assertThat(chunkWithMetadata.metadata()).isEqualTo(fileMetaData);
        verify(fileMetadataRepository, times(1)).findByIdAndIsDeletedFalse(any());
    }

    @Test
    public void testHandleFileUpload() throws IOException {

        MultipartFile multipartFilePart = videoServiceImpl.handleFileUpload(FileMetaDataUtilTest.getMultiPartFile(), 1 , 20);
        assertThat(multipartFilePart).isNull();

        MultipartFile multipartFileCompleted = videoServiceImpl.handleFileUpload(FileMetaDataUtilTest.getMultiPartFile(), 20 , 20);
        assertThat(multipartFileCompleted).isNotNull();
    }

    //TODO we need to add more tests (exception tests, validation tests and others scenarios)
}