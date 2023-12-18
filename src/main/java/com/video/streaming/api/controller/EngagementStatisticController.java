package com.video.streaming.api.controller;

import com.video.streaming.api.entity.EngagementStatistic;
import com.video.streaming.api.repository.EngagementStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/engagement-statistic")
public class EngagementStatisticController {

    @Autowired
    EngagementStatisticRepository engagementStatisticRepository;

    @GetMapping
    public ResponseEntity<List<EngagementStatistic>> getEngagementStatistic(@RequestParam(required = true) String userIdentifier) {
        return new ResponseEntity<>(engagementStatisticRepository.findByUserIdentifier(userIdentifier), HttpStatus.OK);
    }
}
