package com.dannycode.chatApp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dannycode.chatApp.cache.MessageCacheService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cache")
public class CacheStatsController {

    private final MessageCacheService messageCache;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "hits",           messageCache.getHits(),
            "misses",         messageCache.getMisses(),
            "hitRatePercent", Math.round(messageCache.getHitRate() * 10.0) / 10.0,
            "cachedRooms",    messageCache.getSize()
        ));
    }
}
