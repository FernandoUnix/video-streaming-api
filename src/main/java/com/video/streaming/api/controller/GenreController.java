package com.video.streaming.api.controller;

import com.video.streaming.api.enums.Genre;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @GetMapping()
    public List<String> getGenres() {
        return Stream.of(Genre.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
