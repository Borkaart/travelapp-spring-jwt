package com.travelapp.social.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private UserSummaryDto author;
    private String content;
    private LocalDateTime createdAt;
}
