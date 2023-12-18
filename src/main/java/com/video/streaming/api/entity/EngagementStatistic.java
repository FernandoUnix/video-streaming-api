package com.video.streaming.api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document(collection = "engagement-statistic")
public class EngagementStatistic {

    @Id
    private UUID id;
    private String userIdentifier;
    private UUID videoIdentifier;
    private long impressions;
    private long views;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EngagementStatistic() {
    }

    public EngagementStatistic(UUID id) {
        this.id = id;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public UUID getVideoIdentifier() {
        return videoIdentifier;
    }

    public void setVideoIdentifier(UUID videoIdentifier) {
        this.videoIdentifier = videoIdentifier;
    }

    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "EngagementStatistic{" +
                "id='" + id + '\'' +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", videoIdentifier=" + videoIdentifier +
                ", impressions=" + impressions +
                ", views=" + views +
                '}';
    }
}
