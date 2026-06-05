package com.fadreit.inkreadboot.Dto;

import lombok.Data;

@Data
public class Orders_PageListOrdersForAdminRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String status;
    private String keyword;
}
