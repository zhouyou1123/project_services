package com.sioeye.youle.run.pay.yeepay.dao;

import com.sioeye.youle.run.pay.yeepay.config.CustomException;
import com.sioeye.youle.run.pay.yeepay.model.LedgerDetail;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName ILedgerDetail.java
 *
 * @todo 分账详情dao
 */
public interface ILedgerDetailDao {

	/**
	 * 保存分账信息
	 * 
	 * @param ledgerDetail
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             int
	 */
	public int save(LedgerDetail ledgerDetail) throws CustomException, Exception;

	/**
	 * 获取分账信息列表
	 * 
	 * @param yeepayId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             int
	 */
	public int removeLedgerDetailList(String yeepayId) throws CustomException, Exception;

}
