package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.payment.Payment;
import com.sioeye.youle.run.order.domain.payment.PaymentResult;

public interface PaymentService {
    public boolean validatePaid(String orderId, String paymentId, boolean isThirdQueryFlag);
    public PaymentResult createPayment(Payment payment);
}
