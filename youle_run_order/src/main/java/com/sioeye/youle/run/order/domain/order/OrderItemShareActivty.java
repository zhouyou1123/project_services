package com.sioeye.youle.run.order.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemShareActivty {
    private String shareId;
    private String parkShareId;
    private Boolean uploadFlag;
    private String shareUploadUrl;
    private String shareCheckUrl;
}
