package com.fadreit.inkreadboot.Dto;

import lombok.Data;

@Data
public class Announce_GetAllRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private Integer status;
}
