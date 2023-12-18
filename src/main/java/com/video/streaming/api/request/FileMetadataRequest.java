package com.video.streaming.api.request;

import com.video.streaming.api.enums.Genre;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMetadataRequest {

    private UUID id;
    private String title;
    private String synopsis;
    private String director;
    private Map<String, String> cast;
    private Integer yearOfRelease;
    private List<Genre>  genre;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Map<String, String> getCast() {
        return cast;
    }

    public void setCast(Map<String, String> cast) {
        this.cast = cast;
    }

    public Integer getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(Integer yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre>  genre) {
        this.genre = genre;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}