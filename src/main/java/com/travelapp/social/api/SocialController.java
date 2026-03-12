package com.travelapp.social.api;

import com.travelapp.entity.User;
import com.travelapp.social.dto.*;
import com.travelapp.social.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody CreatePostDto dto) {
        return ResponseEntity.ok(socialService.createPost(dto, user.getId()));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> getFeed(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(socialService.getAllPosts(user.getId()));
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(socialService.getUserPosts(userId, user.getId()));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        socialService.likePost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody CreateCommentDto dto) {
        return ResponseEntity.ok(socialService.addComment(postId, dto, user.getId()));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        socialService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
