package com.sioeye.youle.run.order.context;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GoodsResponse {

	private Integer resourceCategory;
	private Integer resourceType;
	private String goodsId;
	private Integer goodsType;
    private String parkId;
    private String parkName;
    private String gameId;
    private String gameName;
    private String seatId;
    private String seatName;
    private String seatSequenceNo;
    private String thumbnailUrl;
    private String previewUrl;
    private String downloadUrl;
    private Integer width;
    private Integer height;
    private Integer size;
    private Integer duration;
    private Date createTime;
    private Date shootingTime;
    private String activityId;
}
