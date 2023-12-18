package com.video.streaming.api.service;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.enums.Genre;
import com.video.streaming.api.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileMetadataService {

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    public Page<FileMetadata> findByIsDeletedFalseOrderByCreateAtDesc(Pageable pageable){

        return this.fileMetadataRepository.findByIsDeletedFalseOrderByCreateAtDesc(pageable);
    }

    public Page<FileMetadata> findByFilters(Integer yearOfRelease, List<Genre> genre, String director, String title, Pageable pageable, MongoTemplate mongoTemplate){

        return this.fileMetadataRepository.findByFilters(yearOfRelease, genre, director, title, pageable, mongoTemplate);
    }

}
