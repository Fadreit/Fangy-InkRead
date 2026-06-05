package com.fadreit.inkreadboot.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 添加购物车返回数据
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart_addCartReturn {
    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String coverUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
