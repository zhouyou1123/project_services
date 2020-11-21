package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.context.CreateOrderRequest;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import com.sioeye.youle.run.order.domain.payment.Payment;
import com.sioeye.youle.run.order.domain.payment.PaymentId;
import com.sioeye.youle.run.order.domain.payment.PaymentResult;
import com.sioeye.youle.run.order.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class CreateOrderPaymentService {
    @Autowired
    private PaymentService paymentService;

    public void createOrderPayment(Supplier<Payment> paymentSupplier,Consumer<PaymentResult> paymentResultConsumer){

        paymentResultConsumer.accept(paymentService.createPayment(paymentSupplier.get()));
    }

}
