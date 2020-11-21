/**  
* <p>Title: ValidateDiscountType.java</p>  
* <p>Description: </p>    
* @author GongGuowei  
* @date 2019年6月12日  
* @version 1.0  
*/  
package com.sioeye.youle.run.order.service.intf;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;

/**
 * @Author GuoGongwei
 * @Time: 2019年6月12日
 * @Description:验证折扣类型：套票、分享活动、园区优惠活动
 */
@Deprecated
public interface IValidateDiscountType {

	/**
	 * 
	 * @Description:验证是否套票支付
	 * @Author GuoGongwei
	 * @Time: 2019年6月12日下午2:10:28
	 * @param params
	 */
	public BuyerCoupon validateCoupon(Map<String, Object> params, Date orderTime, Integer goodsType);
	
	/**
	 * 
	 * @Description:验证是否分享活动支付
	 * @Author GuoGongwei
	 * @Time: 2019年6月12日下午2:11:59
	 * @param params
	 * @return
	 */
	public JSONObject validateShare(Map<String, Object> params, String gameId);
}
