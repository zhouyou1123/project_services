package com.sioeye.youle.run.payment.model;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年5月29日
 *
 * @fileName Payment.java
 *
 * @todo 支付
 */
public class Payment {

	private String objectId = UUID.randomUUID().toString().replace("-", "").toUpperCase();// 唯一编号;
	private Date payTime; // 支付时间
	private Integer payWay; // 支付方式
	private String sign; // 签名
	private String nonceStr;// 随机字符串
	private String tradeType;// 交易类型
	private String prepayId;// 微信预支付订单id
	private String sioeyeSign;// 自定义的sioeye签名
	private String openId;// 用户微信openid
	private Boolean iosSandboxFlag;// 是否是沙箱支付标识
	private Date updateTime;// 更新时间

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getPrepayId() {
		return prepayId;
	}

	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	public String getSioeyeSign() {
		return sioeyeSign;
	}

	public void setSioeyeSign(String sioeyeSign) {
		this.sioeyeSign = sioeyeSign;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Boolean getIosSandboxFlag() {
		return iosSandboxFlag;
	}

	public void setIosSandboxFlag(Boolean iosSandboxFlag) {
		this.iosSandboxFlag = iosSandboxFlag;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
