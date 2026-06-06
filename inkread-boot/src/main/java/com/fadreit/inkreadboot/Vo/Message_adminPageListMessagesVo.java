package com.fadreit.inkreadboot.Vo;

import lombok.Data;

@Data
public class Message_adminPageListMessagesVo {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
}
