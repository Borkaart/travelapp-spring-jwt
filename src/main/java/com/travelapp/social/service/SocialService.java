package com.travelapp.social.service;

import com.travelapp.entity.User;
import com.travelapp.repository.UserRepository;
import com.travelapp.social.dto.*;
import com.travelapp.social.entity.Comment;
import com.travelapp.social.entity.Post;
import com.travelapp.social.repository.CommentRepository;
import com.travelapp.social.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostDto createPost(CreatePostDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .author(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);
        return mapToPostDto(savedPost, userId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts(Long currentUserId) {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> mapToPostDto(post, currentUserId))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PostDto> getUserPosts(Long userId, Long currentUserId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId).stream()
                .map(post -> mapToPostDto(post, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (post.getLikedBy().contains(user)) {
            post.getLikedBy().remove(user);
        } else {
            post.getLikedBy().add(user);
        }
        
        postRepository.save(post);
    }

    @Transactional
    public CommentDto addComment(Long postId, CreateCommentDto dto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .post(post)
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentDto(savedComment);
    }
    
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getAuthor().getId().equals(userId)) {
             throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.delete(post);
    }

    private PostDto mapToPostDto(Post post, Long currentUserId) {
        boolean isLiked = post.getLikedBy().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));

        List<CommentDto> comments = post.getComments().stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());

        return PostDto.builder()
                .id(post.getId())
                .author(mapToUserSummary(post.getAuthor()))
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getLikedBy().size())
                .isLikedByCurrentUser(isLiked)
                .comments(comments)
                .build();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(mapToUserSummary(comment.getAuthor()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private UserSummaryDto mapToUserSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}
