package com.sioeye.youle.run.order.domain.common;

import java.util.Date;

public interface DomainEvent {

    /**
     * 事件版本
     * @return
     */
    public int eventVersion();

    /**
     * 发生日期
     * @return
     */
    public Date occurredOn();
}
