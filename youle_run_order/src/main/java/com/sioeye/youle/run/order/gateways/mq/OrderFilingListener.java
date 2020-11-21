package com.sioeye.youle.run.order.gateways.mq;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sioeye.youle.run.order.application.IGoodsOrderAppService;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.gateways.mq.dto.OrderFilingBackMessage;
import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class OrderFilingListener  implements ChannelAwareMessageListener {
    @Autowired
    private IGoodsOrderAppService appService;
    @Value("${order.filling.copy-response-queue}")
    private String orderFillingCallbackQueue;
//    private JsonService jsonService;
//    @StreamListener(OrderFilingTopic.INPUT)
//    public void receive(OrderFilingBackMessage message, @Header(AmqpHeaders.CHANNEL) Channel channel,
//                        @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag){
//        if (message.getSuccess() == null || !message.getSuccess()){
//            log.info("{" +
//                    "\"queue\":\""+OrderFilingTopic.INPUT + "\"," +
//                    "\"message\":"+ JSONObject.toJSONString(message) +
//                    "}");
//            return;
//        }
//        try
//        {
//            appService.filingBack(message.getOrderId());
//            channel.basicAck(deliveryTag,false);
//        }catch (Exception ex){
//            log.error("{" +
//                    "\"queue\":\""+OrderFilingTopic.INPUT + "\"," +
//                    "\"message\":"+ JSONObject.toJSONString(message) +","+
//                    "\"ack-error\":\""+ex.getMessage() + "\"" +
//                    "}");
//        }
//
//    }

    @Override
    public void onMessage(Message message, Channel channel){

        OrderFilingBackMessage filingBackMessage =  JSONObject.parseObject(new String(message.getBody()),OrderFilingBackMessage.class);
        try
        {
            if (filingBackMessage.getSuccess() == null || !filingBackMessage.getSuccess()) {
                log.info("{" +
                        "\"queue\":\"" + orderFillingCallbackQueue + "\"," +
                        "\"message\":" + new String(message.getBody()) +
                        "}");
            }else{
                appService.filingBack(filingBackMessage.getOrderId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        }
        catch (Exception ex){
            log.error("{" +
                   "\"queue\":\""+orderFillingCallbackQueue+ "\"," +
                    "\"message\":"+ JSONObject.toJSONString(message) +","+
                    "\"ack-error\":\""+ex.getMessage() + "\"" +
                    "}");
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
            }
        }
    }
}
