package com.sioeye.youle.run.order.gateways.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PhotoDto {
    private String objectid;
    private String previewurl;
    private String gamename;
    private String seatid;
    private String gameid;
    private String activityid;
    private String parkname;
    private String parkid;
    private String qrcode;
    private Date shootingtime;
    private String waitsignedurl;
}
