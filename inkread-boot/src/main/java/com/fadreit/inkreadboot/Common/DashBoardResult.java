package com.fadreit.inkreadboot.Common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 仪表盘数据
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardResult<T> {
    private Integer bookCount;
    private Integer categoryCount;
    private Integer pendingOrderCount;
    private Integer userCount;
    private List<T> recentOrders;
}
