package com.video.streaming.api.entity;

import com.video.streaming.api.enums.Genre;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "file-metadata")
public class FileMetadata implements Serializable {

    @Id
    private UUID id;

    private String title;

    private String synopsis;

    private String director;

    private Map<String, String> Cast;

    private Integer yearOfRelease;

    private List<Genre> genre;

    private int runningTime;

    @CreatedDate
    private LocalTime createAt;

    @LastModifiedDate
    private LocalTime updateAt;

    private Long size;

    private String httpContentType;

    private boolean isDeleted;

    public FileMetadata(UUID id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
        return Cast;
    }

    public void setCast(Map<String, String> cast) {
        Cast = cast;
    }

    public Integer getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(Integer yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public List<Genre>  getGenre() {
        return genre;
    }

    public void setGenre(List<Genre>  genre) {
        this.genre = genre;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public LocalTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalTime createAt) {
        this.createAt = createAt;
    }

    public LocalTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalTime updateAt) {
        this.updateAt = updateAt;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public void setHttpContentType(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata that = (FileMetadata) o;
        return runningTime == that.runningTime && isDeleted == that.isDeleted && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(synopsis, that.synopsis) && Objects.equals(director, that.director) && Objects.equals(Cast, that.Cast) && Objects.equals(yearOfRelease, that.yearOfRelease) && Objects.equals(genre, that.genre) && Objects.equals(createAt, that.createAt) && Objects.equals(updateAt, that.updateAt) && Objects.equals(size, that.size) && Objects.equals(httpContentType, that.httpContentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, synopsis, director, Cast, yearOfRelease, genre, runningTime, createAt, updateAt, size, httpContentType, isDeleted);
    }
}
