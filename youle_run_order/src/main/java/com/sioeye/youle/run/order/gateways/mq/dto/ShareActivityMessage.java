package com.sioeye.youle.run.order.gateways.mq.dto;

import lombok.Data;

@Data
public class ShareActivityMessage {
    private String orderId;
    private String seqId;
    //TODO checkUrl,通过消息传递给微视分享活动，再回传回来，避免查询数据库
    private String checkUrl;
    private Integer retry = 0;

}
