package com.sioeye.youle.run.pay.yeepay.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName LedgeDetail.java
 *
 * @todo 易宝支付分账详情
 */
public class LedgerDetail {

	private String objectId = UUID.randomUUID().toString().replace("-", "").toUpperCase();// 唯一编号;
	private String yeepayId;
	private String ledgerNo;
	private String ledgerName;
	private Integer ledgerType;// 分账类型1：金额2：比例
	private BigDecimal amount;
	private BigDecimal proportion;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getYeepayId() {
		return yeepayId;
	}

	public void setYeepayId(String yeepayId) {
		this.yeepayId = yeepayId;
	}

	public String getLedgerNo() {
		return ledgerNo;
	}

	public void setLedgerNo(String ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public Integer getLedgerType() {
		return ledgerType;
	}

	public void setLedgerType(Integer ledgerType) {
		this.ledgerType = ledgerType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getProportion() {
		return proportion;
	}

	public void setProportion(BigDecimal proportion) {
		this.proportion = proportion;
	}

}
