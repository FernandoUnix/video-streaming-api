package com.video.streaming.api.util;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.enums.Genre;
import com.video.streaming.api.request.FileMetadataRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMetaDataUtilTest {

    public static final UUID metadataId = UUID.fromString("85cb36a4-09d4-4856-b178-17163fd26e3e");

    public static final String SYNOPSIS = "Spider-Man is a 2002 American superhero film based on the Marvel Comics character of the same name, created" +
            " by Stan Lee and Steve Ditko. Directed by Sam Raimi from a screenplay by David Koepp, it is the first installment in Raimi's " +
            "Spider-Man trilogy, produced by Columbia Pictures in association with Marvel Enterprises and Laura Ziskin Productions," +
            " and distributed by Sony Pictures Releasing. The film stars Tobey Maguire as the titular character, " +
            "alongside Willem Dafoe, Kirsten Dunst, James Franco, Cliff Robertson, and Rosemary Harris. " +
            "The film chronicles Spider-Man's origin story and early superhero career. After being bitten by a genetically altered spider," +
            " outcast teenager Peter Parker develops spider-like superhuman abilities and adopts a masked superhero identity to fight crime and " +
            "injustice in New York City, facing the sinister Green Goblin in the process.";

    public static FileMetadata getFileMetadata() {
        FileMetadata fileMetadata= new FileMetadata(metadataId);

        fileMetadata.setTitle("Spider-Man");
        fileMetadata.setSynopsis(SYNOPSIS);
        fileMetadata.setYearOfRelease(2002);
        fileMetadata.setDirector("Sam Raimi");

        Map<String, String> cast = new HashMap<>();
        cast.put("Peter Parker/Spider-Man", "Tobey Maguire");
        cast.put("Norman Osborn/Green Goblin", "Willem Dafoe");

        fileMetadata.setCast(cast);
        fileMetadata.setGenre(List.of(Genre.ACTION, Genre.ADVENTURE));

        return fileMetadata;
    }

    public static FileMetadataRequest getFileMetadataRequest() {

        FileMetadataRequest fileMetadataRequest =  new FileMetadataRequest();

        fileMetadataRequest.setTitle("Spider-Man");
        fileMetadataRequest.setSynopsis(SYNOPSIS);
        fileMetadataRequest.setYearOfRelease(2002);
        fileMetadataRequest.setDirector("Sam Raimi");

        Map<String, String> cast = new HashMap<>();
        cast.put("Peter Parker/Spider-Man", "Tobey Maguire");
        cast.put("Norman Osborn/Green Goblin", "Willem Dafoe");

        fileMetadataRequest.setCast(cast);
        fileMetadataRequest.setGenre(List.of(Genre.ACTION, Genre.ADVENTURE));

        return fileMetadataRequest;
    }

    public static MultipartFile getMultiPartFile() throws IOException {

        Resource resource = new ClassPathResource("static/test-video.mp4");
        Path filePath = Paths.get(resource.getURI());
        byte[] video = Files.readAllBytes(filePath);

        return new MockMultipartFile("file", "orig", "video/mp4", video);
    }
}
