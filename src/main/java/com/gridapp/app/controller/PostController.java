package com.gridapp.app.controller;

import com.gridapp.app.entity.Comment;
import com.gridapp.app.entity.Post;
import com.gridapp.app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostService.CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable String postId,
            @RequestBody PostService.AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.saveComment(postId, request));
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getCommentsByPostId(postId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable String postId,
            @RequestParam String authorId) {
        postService.likePost(postId, authorId);
        return ResponseEntity.ok().build();
    }
}
