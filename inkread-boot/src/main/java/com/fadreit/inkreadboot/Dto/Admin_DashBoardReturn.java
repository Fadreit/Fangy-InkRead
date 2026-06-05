package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Admin_DashBoardReturn {
    private Integer id;
    private String orderNo;
    private String username;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
