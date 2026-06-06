package com.fadreit.inkreadboot.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Integer id;
    private Integer userId;
    private String content;
    private Integer likeCount = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted = 0;
}