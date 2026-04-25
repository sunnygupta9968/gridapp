package com.gridapp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    public void processBotInteraction(String botId, String humanUserId) {
        String cooldownKey = "notif_cooldown:user_" + humanUserId;
        String pendingListKey = "user:" + humanUserId + ":pending_notifs";
        String message = "Bot " + botId + " replied to your post";
        Boolean isCooldownActive = redisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(isCooldownActive)) {
            // If YES: Push the message to the right side of the Redis List
            redisTemplate.opsForList().rightPush(pendingListKey, message);
        } else {
            // If NO: Send instantly and set a new 15-minute lock
            System.out.println("Push Notification Sent to User " + humanUserId + ": " + message);
            redisTemplate.opsForValue().set(cooldownKey, "throttled", Duration.ofMinutes(15));
        }
    }

    @Scheduled(fixedRate = 300000)
    public void sweepNotifications() {
        // Find all users with pending notifications
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // Extract the user ID from the Redis key string (e.g., "user:123:pending_notifs")
                String userId = key.split(":")[1];

                // Get the total number of pending notifications
                Long count = redisTemplate.opsForList().size(key);

                if (count != null && count > 0) {
                    System.out.println("Summarized Push Notification for User " + userId +
                            ": A Bot and [" + (count - 1) + "] others interacted with your posts.");

                    // Clear the list so we don't notify them again next time
                    redisTemplate.delete(key);
                }
            }
        }
    }



}
