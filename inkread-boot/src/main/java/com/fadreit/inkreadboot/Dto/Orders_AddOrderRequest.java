package com.fadreit.inkreadboot.Dto;

import lombok.Data;

import java.util.List;

@Data
public class Orders_AddOrderRequest {
    private List<Integer> cartIds;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}
