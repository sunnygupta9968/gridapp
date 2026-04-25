package com.gridapp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisViralityService {
    private final StringRedisTemplate redisTemplate;

    public void incrementScore(String postId, long points) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, points);
        System.out.println("Virality score for post " + postId + " increased by " + points);
    }

    public long getViralityScore(String postId) {
        String key = "post:" + postId + ":virality_score";
        String score = redisTemplate.opsForValue().get(key);
        return score != null ? Long.parseLong(score) : 0L;
    }
}
