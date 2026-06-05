package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Orders_ShipReturn {
    private Integer id;
    private String orderNo;
    private String status;
    private String logisticCompany;
    private String trackingNo;
}
