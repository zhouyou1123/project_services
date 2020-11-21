package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.order.event.PlacedOrderEventMessage;

public interface NotificationService {
    void sendNotification(PlacedOrderEventMessage notificationMessage);
}
