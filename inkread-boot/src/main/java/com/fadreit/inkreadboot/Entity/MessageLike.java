package com.fadreit.inkreadboot.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息点赞实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageLike {
    private Integer id;
    private Integer messageId;
    private Integer userId;
    private LocalDateTime createdAt;
}
