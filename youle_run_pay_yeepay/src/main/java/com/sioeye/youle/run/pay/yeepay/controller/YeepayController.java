package com.sioeye.youle.run.pay.yeepay.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.pay.yeepay.config.CustomException;
import com.sioeye.youle.run.pay.yeepay.config.EnumHandle;
import com.sioeye.youle.run.pay.yeepay.service.intf.IYeepay;
import com.sioeye.youle.run.pay.yeepay.util.LogUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName YeepayController.java
 *
 * @todo 易宝支付controller
 */
@RestController
@RequestMapping("/yeepay")
public class YeepayController {

	private static final Log logger = LogFactory.getLog(YeepayController.class);

	@Autowired
	private IYeepay iYeepay;

	/**
	 * 创建预订单
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/pre_yeepay", method = RequestMethod.POST)
	public String prePayment(@RequestBody Map<String, Object> params) {
		JSONObject result = new JSONObject();
		try {
			JSONObject s=iYeepay.preYeepay(params);
			result = JSONObject.parseObject(LogUtil.packageSuccessLog(logger, s));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			result = LogUtil.BusinessError(logger, new CustomException(EnumHandle.INTERNAL_ERROR));
			LogUtil.internalError(logger, e, params);
		}
		return result.toString();
	}

	@RequestMapping(value = "/get_yeepay_order_detail", method = RequestMethod.POST)
	public String getYeepayOrderDetail(@RequestBody Map<String, Object> params) {
		JSONObject result = new JSONObject();
		try {
			result = JSONObject.parseObject(LogUtil.packageSuccessLog(logger, iYeepay.getYeepayOrderDetail(params)));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			result = LogUtil.BusinessError(logger, new CustomException(EnumHandle.INTERNAL_ERROR));
			LogUtil.internalError(logger, e, params);
		}
		return result.toString();
	}
}
