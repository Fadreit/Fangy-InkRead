package com.fadreit.inkreadboot.Common;

import com.fadreit.inkreadboot.Entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单结果
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResult<T> {
    private Orders order;
    private String username;
    private List<T> items;
}
