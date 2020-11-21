package com.sioeye.youle.run.payment.dao;

import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.model.Payment;

public interface IPaymentDao {

	/**
	 * 保存支付信息
	 * 
	 * @param payment
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public int save(Payment payment) throws CustomException, Exception;

	/**
	 * 获取支付详情
	 * 
	 * @param objectId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public Payment getPaymentDetail(String objectId) throws CustomException, Exception;

	/**
	 * 更新支付信息
	 * 
	 * @param payment
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public int update(Payment payment) throws CustomException, Exception;

	/**
	 * 根据订单id获取订单实际支付金额
	 * 
	 * @param orderId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public String queryActualAmountByOrderId(String orderId) throws CustomException, Exception;
	
	/**
	 * 根据订单id获取支付信息
	 * @param orderId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public Payment queryPaymentByOrderId(String orderId) throws CustomException, Exception;
}
