package com.fadreit.inkreadboot.Vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message_getMessageByIdVo {
    private Integer id;
    private Integer userId;
    private String username;
    private String nickname;
    private String content;
    private Integer likeCount;
    private boolean liked = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
