package com.sioeye.youle.run.order.gateways.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ClipDto {
    private String objectid;
    private String downloadurl;
    private String previewurl;
    private Date starttime;
    private Integer size;
    private Integer duration;
    private Integer width;
    private Integer height;
    private String devicename;
    private String gamename;
    private String gameid;
    private String seatid;
    private String activityid;
    private String parkname;
    private String parkid;
    private String thumbnailurl;
    private String qrcode;
}
