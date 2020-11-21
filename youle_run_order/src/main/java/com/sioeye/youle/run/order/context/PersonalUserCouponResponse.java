package com.sioeye.youle.run.order.context;

import java.util.Date;

import lombok.Data;

@Data
public class PersonalUserCouponResponse {

	private String objectid;
	private String userid;
	private String amusementparkid;
	private String parkname;
	private String openid;
	private String timezone;
	private Date startdate;
	private Date enddate;
	private Date updatetime;
	private Integer[] canbuygoodstypes;
	private String orderid;
}
