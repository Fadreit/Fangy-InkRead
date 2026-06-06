package com.fadreit.inkreadboot.Dto;

import lombok.Data;

@Data
public class Announcement_AddRequest {
    private String title;
    private String content;
    private Integer status = 0;
}
