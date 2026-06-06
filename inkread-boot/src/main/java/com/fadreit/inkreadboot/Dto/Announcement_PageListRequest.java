package com.fadreit.inkreadboot.Dto;

import lombok.Data;

@Data
public class Announcement_PageListRequest {
    private Integer page = 1;
    private Integer size = 10;
}
