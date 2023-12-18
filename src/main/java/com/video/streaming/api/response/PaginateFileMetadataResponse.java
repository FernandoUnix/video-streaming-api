package com.video.streaming.api.response;

import java.util.List;

public class PaginateFileMetadataResponse {

    private int actualPage;
    private Long totalItems;
    private int totalPage;
    private List<FileMetadataResponse> videos;

    public int getActualPage() {
        return actualPage;
    }

    public void setActualPage(int actualPage) {
        this.actualPage = actualPage;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<FileMetadataResponse> getVideos() {
        return videos;
    }

    public void setVideos(List<FileMetadataResponse> videos) {
        this.videos = videos;
    }
}
