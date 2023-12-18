package com.video.streaming.api.repository;

import com.video.streaming.api.entity.FileMetadata;
import com.video.streaming.api.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, UUID> {

    Page<FileMetadata> findByIsDeletedFalseOrderByCreateAtDesc(Pageable pageable);
    Optional<FileMetadata> findByIdAndIsDeletedFalse(UUID id);

    default Page<FileMetadata> findByFilters(Integer yearOfRelease, List<Genre> genre, String director, String title, Pageable pageable, MongoTemplate mongoTemplate) {

        Query query = new Query(Criteria.where("isDeleted").is(false));

        if (yearOfRelease != null) {
            query.addCriteria(Criteria.where("yearOfRelease").is(yearOfRelease));
        }

        if (genre != null && !genre.isEmpty()) {
            query.addCriteria(Criteria.where("genre").in(genre));
        }

        if (director != null && !director.isEmpty()) {
            query.addCriteria(Criteria.where("director").regex(director, "i"));
        }

        if (title != null && !title.isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }

        query.with(pageable);

        return new PageImpl<>(mongoTemplate.find(query, FileMetadata.class), pageable, mongoTemplate.count(query, FileMetadata.class));
    }
}
