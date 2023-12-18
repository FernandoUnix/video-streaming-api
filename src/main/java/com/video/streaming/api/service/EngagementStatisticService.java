package com.video.streaming.api.service;

import com.video.streaming.api.entity.EngagementStatistic;
import com.video.streaming.api.repository.EngagementStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EngagementStatisticService {

    @Autowired
    private EngagementStatisticRepository engagementStatisticRepository;

    public void addImpressions(String userIdentifier, UUID videoIdentifier) {

        Optional<EngagementStatistic> engagementStatistic = engagementStatisticRepository.findByUserIdentifierAndVideoIdentifier(userIdentifier, videoIdentifier);

        EngagementStatistic newEngagementStatistic = new EngagementStatistic();
        newEngagementStatistic.setUserIdentifier(userIdentifier);
        newEngagementStatistic.setVideoIdentifier(videoIdentifier);

        if (engagementStatistic.isPresent()) {
            newEngagementStatistic.setId(engagementStatistic.get().getId());
            newEngagementStatistic.setImpressions(engagementStatistic.get().getImpressions() + 1);
            newEngagementStatistic.setViews(engagementStatistic.get().getViews());
        } else {
            newEngagementStatistic.setId(UUID.randomUUID());
            newEngagementStatistic.setImpressions(0);
            newEngagementStatistic.setViews(0);
        }

        engagementStatisticRepository.save(newEngagementStatistic);
    }

    public void addViews(String userIdentifier, UUID videoIdentifier) {

        Optional<EngagementStatistic> engagementStatistic = engagementStatisticRepository.findByUserIdentifierAndVideoIdentifier(userIdentifier, videoIdentifier);

        EngagementStatistic newEngagementStatistic = new EngagementStatistic();
        newEngagementStatistic.setUserIdentifier(userIdentifier);
        newEngagementStatistic.setVideoIdentifier(videoIdentifier);

        if (engagementStatistic.isPresent()) {
            newEngagementStatistic.setId(engagementStatistic.get().getId());
            newEngagementStatistic.setViews(engagementStatistic.get().getViews() + 1);
            newEngagementStatistic.setImpressions(engagementStatistic.get().getImpressions());
        } else {
            newEngagementStatistic.setId(UUID.randomUUID());
            newEngagementStatistic.setImpressions(0);
            newEngagementStatistic.setViews(0);
        }

        engagementStatisticRepository.save(newEngagementStatistic);
    }
}
