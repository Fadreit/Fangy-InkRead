package com.fadreit.inkreadboot.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer bookId;
    private String bookTitle;
    private String bookCover;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}