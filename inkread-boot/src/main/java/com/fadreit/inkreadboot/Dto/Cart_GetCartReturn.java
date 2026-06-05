package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 查询购物车返回数据
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart_GetCartReturn {
    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String coverUrl;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private BigDecimal subtotal;
    private String selected;
    private LocalDateTime createdAt;
}
