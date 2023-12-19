package com.video.streaming.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.service.EngagementStatisticService;
import com.video.streaming.api.service.FileMetadataService;
import com.video.streaming.api.service.VideoService;
import com.video.streaming.api.service.VideoServiceImpl;
import com.video.streaming.api.util.FileMetaDataUtilTest;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(VideoController.class)
public class VideoControllerTest {

    private static final String BASE_URL = "/v1/videos";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private FileMetadataService FileMetadataService;

    @MockBean
    private EngagementStatisticService engagementStatisticService;

    @MockBean
    private MongoTemplate mongoTemplate;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testSaveMetaData() throws Exception {

        Mockito.when(videoService.save(any())).thenReturn(FileMetaDataUtilTest.metadataId);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(FileMetaDataUtilTest.getFileMetadataRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"85cb36a4-09d4-4856-b178-17163fd26e3e\""));

    }

    @Test
    public void testUpdateMetaData() throws Exception {

        Mockito.when(videoService.save(any())).thenReturn(FileMetaDataUtilTest.metadataId);

        mockMvc.perform(put(BASE_URL +"/"+FileMetaDataUtilTest.metadataId)
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(FileMetaDataUtilTest.getFileMetadataRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("\"85cb36a4-09d4-4856-b178-17163fd26e3e\""));
    }

    @Test
    public void testUploadFile() throws Exception {

        mockMvc.perform(multipart(BASE_URL + "/upload-file")
                        .file((MockMultipartFile) FileMetaDataUtilTest.getMultiPartFile())
                                .param("id" , FileMetaDataUtilTest.metadataId.toString())
                                .param("type" , "video/mp4")
                                .param("chunkNumber" , "1")
                                .param("totalChunks" , "20"))
                .andExpect(status().isOk())
                .andExpect(content().string("Chunk uploaded successfully"));
    }

    @Test
    public void testDeleteVideo() throws Exception {

        mockMvc.perform(delete(BASE_URL +"/"+ FileMetaDataUtilTest.metadataId)
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(FileMetaDataUtilTest.getFileMetadataRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Video deleted successfully"));
    }

    @Test
    public void testPlayVideo() throws Exception {

        FileMetadata metadata  = FileMetaDataUtilTest.getFileMetadata();
        metadata.setHttpContentType("video/mp4");
        metadata.setSize(FileMetaDataUtilTest.getMultiPartFile().getSize());

        byte[] chunk = FileMetaDataUtilTest.getMultiPartFile().getInputStream().readAllBytes();

        Mockito.when(videoService.fetchChunk(any(), any())).thenReturn(new VideoServiceImpl.ChunkWithMetadata(metadata, chunk));

        mockMvc.perform(get(BASE_URL +"/play/"+ FileMetaDataUtilTest.metadataId)
                        .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(FileMetaDataUtilTest.getFileMetadataRequest()))
                        .header("range", "1234567-"))
                .andExpect(status().isPartialContent());
    }

    @Test
    public void testGetVideoMetaData() throws Exception {

        Mockito.when(videoService.getMetadata(any())).thenReturn(FileMetaDataUtilTest.getFileMetadata());

        MvcResult result = mockMvc.perform(get(BASE_URL +"/"+ FileMetaDataUtilTest.metadataId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        String expectedContent = "{\"id\":\"85cb36a4-09d4-4856-b178-17163fd26e3e\",\"videoUrl\":null,\"title\":\"Spider-Man\",\"synopsis\":\"Spider-Man is a 2002 American superhero film based on the Marvel Comics character of the same name, created by Stan Lee and Steve Ditko. Directed by Sam Raimi from a screenplay by David Koepp, it is the first installment in Raimi's Spider-Man trilogy, produced by Columbia Pictures in association with Marvel Enterprises and Laura Ziskin Productions, and distributed by Sony Pictures Releasing. The film stars Tobey Maguire as the titular character, alongside Willem Dafoe, Kirsten Dunst, James Franco, Cliff Robertson, and Rosemary Harris. The film chronicles Spider-Man's origin story and early superhero career. After being bitten by a genetically altered spider, outcast teenager Peter Parker develops spider-like superhuman abilities and adopts a masked superhero identity to fight crime and injustice in New York City, facing the sinister Green Goblin in the process.\",\"director\":\"Sam Raimi\",\"yearOfRelease\":2002,\"genre\":[\"ACTION\",\"ADVENTURE\"],\"runningTime\":\"00:00:00\",\"httpContentType\":null,\"size\":null,\"hasVideo\":false,\"cast\":{\"Peter Parker/Spider-Man\":\"Tobey Maguire\",\"Norman Osborn/Green Goblin\":\"Willem Dafoe\"}}";

        assertThat(content).isEqualTo(expectedContent);
    }

    //TODO we need to add more tests (exception tests, validation tests and others scenarios)
}