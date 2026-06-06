package com.fadreit.inkreadboot.Common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResult {
    private Integer id;
    private Integer userId;
    private String username;
    private String nickname;
    private String content;
    private Integer likeCount;
    private boolean liked = false;
    private LocalDateTime createdAt;
}
