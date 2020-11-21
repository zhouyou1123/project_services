package com.sioeye.youle.run.payment.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumPayWay;
import com.sioeye.youle.run.payment.service.intf.IPaymentBase;
import com.sioeye.youle.run.payment.util.KeyUtil;
import com.sioeye.youle.run.payment.util.LogUtil;
import com.sioeye.youle.run.payment.util.Util;
import com.sioeye.youle.run.payment.wxpay.PayCommonUtil;
import com.yeepay.g3.sdk.yop.encrypt.DigitalEnvelopeDTO;
import com.yeepay.g3.sdk.yop.utils.DigitalEnvelopeUtils;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private IPaymentBase iPayment;
	@Value("${yeepay.public.key}")
	private String yeepayPublicKey;
	@Value("${yeepay.sioeye.private.key}")
	private String yeepaySioeyePrivateKey;

	private static final Log logger = LogFactory.getLog(PaymentController.class);

	/**
	 * 创建预订单
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/pre_payment", method = RequestMethod.POST)
	public String prePayment(@RequestBody Map<String, Object> parameter) {
		JSONObject result = new JSONObject();
		try {
			result = JSONObject.parseObject(LogUtil.packageSuccessLog(logger, iPayment.prePayment(parameter)));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			result = LogUtil.BusinessError(logger, new CustomException(EnumHandle.INTERNAL_ERROR));
			LogUtil.internalError(logger, e, parameter);
		}
		return result.toString();
	}

	/**
	 * 获取订单详情
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/get_payment_detail", method = RequestMethod.POST)
	public String getPaymentDetail(@RequestBody Map<String, Object> parameter) {
		JSONObject result = new JSONObject();
		try {
			result = JSONObject.parseObject(LogUtil.packageSuccessLog(logger, iPayment.getPaymentDetail(parameter)));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			result = LogUtil.BusinessError(logger, new CustomException(EnumHandle.INTERNAL_ERROR));
			LogUtil.internalError(logger, e, parameter);
		}
		return result.toString();
	}

	/**
	 * @api {POST} /payment/payment_back_weixin 微信支付回调
	 * @apiName payment_back_weixin
	 * @apiGroup payment
	 * @apiVersion 0.0.1
	 * @apiDescription 微信支付回调
	 * @apiParam {xml} request 微信支付回调信息(这里的具体回调方法如有疑问,请当面咨询开发人员)
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 微信支付成功返回数据
	 * @apiSuccessExample {xml} 微信支付成功返回值:
	 *                    <xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[success]]></return_msg></xml>
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 微信支付失败返回值:
	 *                  tip:这里接口调用返回值只有success|fail,因为该接口需要返回微信服务器信息,具体的错误需要到日志里面查看
	 *                  {"110210":"微信支付回调参数错误","110208":"查询订单失败","110211":"支付回调签名错误",
	 *                  "110203":"交易类型错误","110212":"微信支付回调修改支付信息失败","110209":"更新支付信息失败"}
	 */
	@RequestMapping(value = "/payment_back_weixin", method = RequestMethod.POST)
	public void paymentBackWeixin(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObject = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 获取微信回调的orders信息
			BufferedReader reader = request.getReader();
			String line = "";
			StringBuffer inputString = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				inputString.append(line);
			}
			reader.close();
			// 验证返回数据是否是xml格式
			if (!Util.isXML(inputString.toString())) {
				throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
			}
			// 返回的数据，格式化为map格式
			map = PayCommonUtil.doXMLParse(inputString.toString());
			logger.info("weixin pay call back map:" + map);
			// 增加支付方式
			map.put("payWay", EnumPayWay.WEIXIN.getCode().toString());
			// 调用方法
			jsonObject = iPayment.payBack(map);
			LogUtil.packageSuccessLog(logger, jsonObject);
			PrintWriter writer = response.getWriter();
			writer.write(PayCommonUtil.setXML("SUCCESS", "success"));
			writer.flush();
			writer.close();
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write(PayCommonUtil.setXML("fail", "fail"));
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			LogUtil.internalError(logger, e, null);
			// 判断map的字段是否交易成功，根据结果向微信发送SUCCESS或者FAIL的code
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write(PayCommonUtil.setXML("fail", "fail"));
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @api {POST} /payment/payment_back_yeepay 易宝支付回调
	 * @apiName payment_back_yeepay
	 * @apiGroup payment
	 * @apiVersion 0.0.1
	 * @apiDescription 易宝支付回调
	 * @apiParam {xml} request 易宝支付回调信息(这里的具体回调方法如有疑问,请当面咨询开发人员)
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 微信支付成功返回数据
	 * @apiSuccessExample {xml} 易宝支付成功返回值:
	 *                    <xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[success]]></return_msg></xml>
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 易宝支付失败返回值:
	 *                  tip:这里接口调用返回值只有success|fail,因为该接口需要返回微信服务器信息,具体的错误需要到日志里面查看
	 *                  {"110210":"微信支付回调参数错误","110208":"查询订单失败","110211":"支付回调签名错误",
	 *                  "110203":"交易类型错误","110212":"微信支付回调修改支付信息失败","110209":"更新支付信息失败"}
	 */
	@RequestMapping(value = "/payment_back_yeepay", method = RequestMethod.POST)
	public void paymentBackYeepay(HttpServletRequest request, HttpServletResponse response) {
		// 获取回调数据
		String responseMsg = request.getParameter("response");
		logger.info("responseMsg:" + responseMsg);
		DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
		dto.setCipherText(responseMsg);
		try {
			PublicKey publicKey = KeyUtil.getPublicKey(yeepayPublicKey);
			PrivateKey privateKey = KeyUtil.getPrivateKey(yeepaySioeyePrivateKey);
			// 解密验签
			dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
			// 获取回调数据
			logger.info("yeepay pay call back plainText:" + dto.getPlainText());
			JSONObject jsonObject = JSONObject.parseObject(dto.getPlainText());
			// 转换数据
			Map<String, String> map = Util.json2Map(jsonObject);
			// 增加支付方式
			map.put("payWay", EnumPayWay.YEEPAY.getCode().toString());
			jsonObject = iPayment.payBack(map);
			LogUtil.packageSuccessLog(logger, jsonObject);
			PrintWriter writer = response.getWriter();
			writer.write("SUCCESS");
			writer.flush();
			writer.close();
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write("FAIL");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			LogUtil.internalError(logger, e, null);
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write("FAIL");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @api {POST} /payment/divide_back_yeepay 易宝分账回调
	 * @apiName divide_back_yeepay
	 * @apiGroup payment
	 * @apiVersion 0.0.1
	 * @apiDescription 易宝分账回调
	 * @apiParam {xml} request 易宝分账回调回调信息(这里的具体回调方法如有疑问,请当面咨询开发人员)
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 微信支付成功返回数据
	 * @apiSuccessExample {xml} 易宝支付成功返回值:
	 *                    <xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[success]]></return_msg></xml>
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 易宝支付失败返回值:
	 *                  tip:这里接口调用返回值只有success|fail,因为该接口需要返回微信服务器信息,具体的错误需要到日志里面查看
	 *                  {"110210":"微信支付回调参数错误","110208":"查询订单失败","110211":"支付回调签名错误",
	 *                  "110203":"交易类型错误","110212":"微信支付回调修改支付信息失败","110209":"更新支付信息失败"}
	 */
	@RequestMapping(value = "/divide_back_yeepay", method = RequestMethod.POST)
	public void divideBackYeepay(HttpServletRequest request, HttpServletResponse response) {
		// 获取回调数据
		logger.info("yeepay divide call back");
		JSONObject jsonObject = new JSONObject();
		String responseMsg = request.getParameter("response");
		logger.info("yeepay divide responseMsg:" + responseMsg);
		DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
		dto.setCipherText(responseMsg);
		try {
			PublicKey publicKey = KeyUtil.getPublicKey(yeepayPublicKey);
			PrivateKey privateKey = KeyUtil.getPrivateKey(yeepaySioeyePrivateKey);
			// 解密验签
			dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
			// 获取回调数据
			logger.info("yeepay divide plainText:" + dto.getPlainText());
			// jsonMap = parseResponse(dto.getPlainText());
			LogUtil.packageSuccessLog(logger, jsonObject);
			PrintWriter writer = response.getWriter();
			writer.write("SUCCESS");
			writer.flush();
			writer.close();
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write("FAIL");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			LogUtil.internalError(logger, e, null);
			response.reset();
			try {
				PrintWriter writer = response.getWriter();
				writer.write("FAIL");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
