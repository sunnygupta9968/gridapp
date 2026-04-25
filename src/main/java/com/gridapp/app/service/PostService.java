package com.gridapp.app.service;

import com.gridapp.app.entity.Comment;
import com.gridapp.app.entity.Post;
import com.gridapp.app.repository.CommentRepo;
import com.gridapp.app.repository.PostRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final StringRedisTemplate redisTemplate;
    private final RedisViralityService redisViralityService;
    private final NotificationService notificationService;

    public Post savePost(CreatePostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.authorId());
        post.setAuthorType(request.authorType());
        post.setContent(request.content());
        return postRepo.save(post);
    }

    public Post getPost(String postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    public Comment saveComment(String postId, AddCommentRequest request) {
        Post post=postRepo.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));;

        if(request.depthLevel>20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Depth level exceeded");
        }
        if(request.isBot) {
                String botCountKey = "post:" + postId + ":bot_count";
                Long currentCount = redisTemplate.opsForValue().increment(botCountKey);
                if (currentCount > 100) {
                    redisTemplate.opsForValue().decrement(botCountKey);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post has reached maximum bot replies.");
                }
                String coolDownKey = "cooldown:bot_" + request.authorId + ":human_" + post.getAuthorId();
                Boolean isAllowed = (Boolean) redisTemplate.opsForValue().setIfAbsent(coolDownKey, "locked", Duration.ofMinutes(10));
                if (!isAllowed) {
                    redisTemplate.opsForValue().decrement(botCountKey);
                    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot cooldown active for this user");
                }
            }
            Comment comment = new Comment();
            comment.setPost(post);
            comment.setAuthorId(request.authorId());
            comment.setContent(request.content());
            comment.setDepthLevel(request.depthLevel());
            Comment savedComment = commentRepo.save(comment);
            long points = request.isBot() ? 1L : 50L;
            redisViralityService.incrementScore(postId, points);
            if (request.isBot()) {
                notificationService.processBotInteraction(request.authorId(), post.getAuthorId());
            }
            return savedComment;
    }

    public void likePost(String postId, String authorId) {
        String postLikesKey = "post:" + postId + ":liked_by";
        Long addedCount = redisTemplate.opsForSet().add(postLikesKey, authorId);
        if (addedCount != null && addedCount == 1) {
            redisViralityService.incrementScore(postId, 20L);
            System.out.println("User " + authorId + " liked the post. +20 points!");
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already liked this post.");
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(String postId) {
        Post post=postRepo.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));;
        if(post!=null) {
            return commentRepo.findByPost_Id(postId);
        }
        return null;
    }


    public record CreatePostRequest(String authorId, Post.AuthorType authorType, String content) {}
    public record AddCommentRequest(String authorId, String content, long depthLevel, boolean isBot) {}

}
