package com.sioeye.youle.run.pay.yeepay.dao;

import com.sioeye.youle.run.pay.yeepay.config.CustomException;
import com.sioeye.youle.run.pay.yeepay.model.Yeepay;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName IYeepayDao.java
 *
 * @todo 易宝支付dao
 */
public interface IYeepayDao {

	/**
	 * 保存易宝支付信息
	 * 
	 * @param yeepay
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             int
	 */
	public int save(Yeepay yeepay) throws CustomException, Exception;

	/**
	 * 更新易宝支付信息
	 * 
	 * @param yeepay
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             int
	 */
	public int update(Yeepay yeepay) throws CustomException, Exception;

	/**
	 * 根据paymentId查询易宝支付
	 * 
	 * @param paymentId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             Yeepay
	 */
	public Yeepay getYeepayByPaymentId(String paymentId) throws CustomException, Exception;
}