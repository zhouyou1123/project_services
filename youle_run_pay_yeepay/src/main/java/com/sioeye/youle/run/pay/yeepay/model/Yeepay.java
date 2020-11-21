package com.sioeye.youle.run.pay.yeepay.model;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName Yeepay.java
 *
 * @todo 易宝支付
 */
public class Yeepay {

	private String objectId = UUID.randomUUID().toString().replace("-", "").toUpperCase();// 唯一编号;
	private String uniqueOrderNo;
	private String paymentId;
	private String token;
	private String goodsParamExt;// 商品拓展新兴
	private String parentMerchantNo;
	private String merchantNo;
	private String fundProcessType;// 资金处理类型
	private Date createTime;// 创建时间
	private Date updateTime;// 更新时间

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getUniqueOrderNo() {
		return uniqueOrderNo;
	}

	public void setUniqueOrderNo(String uniqueOrderNo) {
		this.uniqueOrderNo = uniqueOrderNo;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGoodsParamExt() {
		return goodsParamExt;
	}

	public void setGoodsParamExt(String goodsParamExt) {
		this.goodsParamExt = goodsParamExt;
	}

	public String getParentMerchantNo() {
		return parentMerchantNo;
	}

	public void setParentMerchantNo(String parentMerchantNo) {
		this.parentMerchantNo = parentMerchantNo;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getFundProcessType() {
		return fundProcessType;
	}

	public void setFundProcessType(String fundProcessType) {
		this.fundProcessType = fundProcessType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
