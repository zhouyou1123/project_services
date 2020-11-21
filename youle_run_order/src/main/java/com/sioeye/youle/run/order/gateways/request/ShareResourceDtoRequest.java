package com.sioeye.youle.run.order.gateways.request;

import lombok.Data;

@Data
public class ShareResourceDtoRequest {
    private String seq;
    private String video_url;
    private String cover_url;
    private String title;
    private Integer width;
    private Integer height;
    private Integer duration;
    private String gameId;
    private String checkUrl;
}
