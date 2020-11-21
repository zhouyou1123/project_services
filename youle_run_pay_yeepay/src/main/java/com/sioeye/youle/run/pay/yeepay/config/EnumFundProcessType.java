package com.sioeye.youle.run.pay.yeepay.config;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName EnumFundProcessType.java
 *
 * @todo 资金处理类型
 */
public enum EnumFundProcessType {
	DELAY_SETTLE, // 延迟结算
	REAL_TIME, // 实时订单
	REAL_TIME_DIVIDE, // 实时分账
	SPLIT_ACCOUNT_IN;// 实时拆分入账
}
