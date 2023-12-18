package com.video.streaming.api.repository;

import com.video.streaming.api.entity.EngagementStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EngagementStatisticRepository extends MongoRepository<EngagementStatistic, UUID> {

    List<EngagementStatistic> findByUserIdentifier(String userIdentifier);
    Optional<EngagementStatistic> findByUserIdentifierAndVideoIdentifier(String userIdentifier, UUID videoIdentifier);
}
