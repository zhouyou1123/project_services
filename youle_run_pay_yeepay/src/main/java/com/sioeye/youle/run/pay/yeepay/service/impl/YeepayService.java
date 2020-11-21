package com.sioeye.youle.run.pay.yeepay.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.pay.yeepay.config.CustomException;
import com.sioeye.youle.run.pay.yeepay.config.EnumFundProcessType;
import com.sioeye.youle.run.pay.yeepay.config.EnumHandle;
import com.sioeye.youle.run.pay.yeepay.config.EnumLedgerType;
import com.sioeye.youle.run.pay.yeepay.config.EnumPayTool;
import com.sioeye.youle.run.pay.yeepay.config.EnumPayType;
import com.sioeye.youle.run.pay.yeepay.config.EnumReportFee;
import com.sioeye.youle.run.pay.yeepay.dao.ILedgerDetailDao;
import com.sioeye.youle.run.pay.yeepay.dao.IYeepayDao;
import com.sioeye.youle.run.pay.yeepay.model.LedgerDetail;
import com.sioeye.youle.run.pay.yeepay.model.Yeepay;
import com.sioeye.youle.run.pay.yeepay.service.intf.IYeepay;
import com.sioeye.youle.run.pay.yeepay.util.ConstUtil;
import com.sioeye.youle.run.pay.yeepay.util.HttpUtil;
import com.sioeye.youle.run.pay.yeepay.util.ValidateUtil;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.client.YopRsaClient;
import com.yeepay.g3.sdk.yop.error.YopError;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName YeepayService.java
 *
 * @todo 易宝支付实现类
 */
@RefreshScope
@Service
public class YeepayService implements IYeepay {

	@Value("${yeepay.query.order.url}")
	private String yeepayQueryOrderUrl;
	@Value("${yeepay.trade.order.url}")
	private String yeepayTradeOrderUrl;
	@Value("${yeepay.api.pay.url}")
	private String yeepayApiPayUrl;
	@Value("${yeepay.notify.url}")
	private String yeepayNotifyUrl;
	@Value("${yeepay.divide.notify.url}")
	private String yeepayDivideNotifyUrl;
	@Value("${yeepay.merchant.number}")
	private String yeepayMerchantNo;
	@Value("${yeepay.merchant.parent.number}")
	private String yeepayMerchantParentNo;
	@Value("${yeepay.app.key}")
	private String yeepayAppKey;
	@Value("${yeepay.sioeye.private.key}")
	private String yeepaySioeyePrivateKey;
	@Value("${weixin.app.id}")
	private String weixinAppId;
	@Value("${yeepay.access.key.id}")
	private String yeepayAccessKeyId;
	@Value("${yeepay.access.key.secret}")
	private String yeepayAccessKeySecret;
	@Value("${yeepay.park.plans.url}")
	private String yeepayParkPlansUrl;
	@Value("${yeepay.park.divide}")
	private boolean yeepayParkDivide;
	@Autowired
	private IYeepayDao iYeepayDao;
	@Autowired
	private ILedgerDetailDao iLedgerDetailDao;

	@Override
	public JSONObject preYeepay(Map<String, Object> params) throws CustomException, IOException, Exception {
		// 验证参数
		this.validateParams(params);
		// 验证是否已下单
		JSONObject tradeOrderJson = new JSONObject();
		Yeepay yeepay = this.validateYeepay(params.get("paymentId").toString(), tradeOrderJson);
		if (yeepay == null) {
			// 创建token和uniqueOrderId
			tradeOrderJson = this.yeepayTradeOrder(params);
		}
		// 聚合收银台api
		JSONObject apiPayJson = this.yeepayApiPay(tradeOrderJson, params);
		// 保存数据
		this.saveYeepay(yeepay, params, tradeOrderJson);
		// 封装返回值
		return this.packagePreYeepay(tradeOrderJson, apiPayJson);
	}

	/**
	 * 验证参数
	 * 
	 * @param params
	 *            void
	 */
	private void validateParams(Map<String, Object> params) {
		// 验证游乐园id
		ValidateUtil.validateEmptyAndString(params.get("amusementParkId"), EnumHandle.PARAMS_AMUSEMENTPARKID_ERROR);
		// 验证订单号
		ValidateUtil.validateEmptyAndString(params.get("orderId"), EnumHandle.PARAMS_ORDERID_ERROR);
		// 验证订单金额
		ValidateUtil.validateEmptyAndPrice(params.get("actualAmount"), EnumHandle.PARAMS_AMOUNT_ERROR);
		// 验证商品名称
		ValidateUtil.validateEmptyAndString(params.get("goodsName"), EnumHandle.PARAMS_GOODSNAME_ERROR);
		// 验证商品详情
		ValidateUtil.validateEmptyAndString(params.get("goodsDesc"), EnumHandle.PARAMS_GOODSDESC_ERROR);
		// 验证openid
		ValidateUtil.validateEmptyAndString(params.get("openId"), EnumHandle.PARAMS_OPENID_ERROR);
		// 验证userip
		ValidateUtil.validateEmptyAndString(params.get("spbillCreateIp"), EnumHandle.PARAMS_USERIP_ERROR);
	}

	/**
	 * 验证payment是否已创建支付
	 * 
	 * @param paymentId
	 *            void
	 * @throws Exception
	 * @throws CustomException
	 */
	private Yeepay validateYeepay(String paymentId, JSONObject tradeOrderJson) throws CustomException, Exception {
		// 插入易宝支付信息
		Yeepay yeepay = iYeepayDao.getYeepayByPaymentId(paymentId);
		if (yeepay == null) {
			return null;
		}
		tradeOrderJson.put("token", yeepay.getToken());
		return yeepay;
	}

	/**
	 * 创建订单
	 * 
	 * @param params
	 * @return
	 * @throws CustomException
	 * @throws IOException
	 * @throws Exception
	 *             JSONObject
	 */
	private JSONObject yeepayTradeOrder(Map<String, Object> params) throws CustomException, IOException, Exception {
		JSONObject result = new JSONObject();
		YopRequest request = new YopRequest(yeepayAppKey, yeepaySioeyePrivateKey);
		// 演示普通参数传递
		request.addParam("parentMerchantNo", yeepayMerchantParentNo);
		request.addParam("merchantNo", yeepayMerchantNo);
		request.addParam("orderId", params.get("orderId").toString());
		request.addParam("orderAmount", params.get("actualAmount").toString());
		request.addParam("notifyUrl", yeepayNotifyUrl);
		if (params.get("timeoutExpress") != null) {
			request.addParam("timeoutExpress", params.get("timeoutExpress"));
		}
		// 封装商品详情
		JSONObject goodsParamExtJson = new JSONObject();
		goodsParamExtJson.put("goodsName", params.get("goodsName"));
		goodsParamExtJson.put("goodsDesc", params.get("goodsDesc"));
		request.addParam("goodsParamExt", goodsParamExtJson.toJSONString());
		// 获取分账明细
		JSONArray divideDetail = null;
		if (yeepayParkDivide) {
			divideDetail = this.getDivideDetail(params.get("actualAmount").toString(),
					params.get("amusementParkId").toString());
		}
		if (divideDetail != null && divideDetail.size() > 0) {
			// 实时分账订单
			request.addParam("fundProcessType", EnumFundProcessType.REAL_TIME_DIVIDE.toString());
			request.addParam("divideDetail", divideDetail.toJSONString());
			request.addParam("divideNotifyUrl", yeepayDivideNotifyUrl);
		}else{
			// 实时订单
			request.addParam("fundProcessType", EnumFundProcessType.REAL_TIME.toString());
		}
		YopResponse response = YopRsaClient.post(yeepayTradeOrderUrl, request);
		JSONObject yeepayJson = JSONObject.parseObject(response.getStringResult());
		if (!response.isSuccess() || !ConstUtil.YEEPAY_API_RETURN_SUCCESS.equals(yeepayJson.getString("code"))) {
			YopError yopError = response.getError();
			if (yopError != null) {
				EnumHandle.YEEPAY_TRADE_ORDER_FAILURE.setCode(yopError.getCode());
				EnumHandle.YEEPAY_TRADE_ORDER_FAILURE.setMessage(yopError.getMessage());
			} else {
				JSONObject resultJson = JSONObject.parseObject(response.getStringResult());
				EnumHandle.YEEPAY_TRADE_ORDER_FAILURE.setCode(resultJson.getString("code"));
				EnumHandle.YEEPAY_TRADE_ORDER_FAILURE.setMessage(resultJson.getString("message"));
			}
			throw new CustomException(EnumHandle.YEEPAY_TRADE_ORDER_FAILURE);
		}
		// 封装返回值
		result.put("orderId", yeepayJson.getString("orderId"));
		result.put("uniqueOrderNo", yeepayJson.getString("uniqueOrderNo"));
		result.put("goodsParamExt", yeepayJson.getJSONObject("goodsParamExt"));
		result.put("token", yeepayJson.getString("token"));
		result.put("fundProcessType", yeepayJson.getString("fundProcessType"));
		result.put("divideDetail", divideDetail);
		return result;
	}

	/**
	 * 获取分账详情，包括分账方编号，金额比例
	 * 
	 * @return JSONObject
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private JSONArray getDivideDetail(String actualAmount, String amusementParkId)
			throws NoSuchAlgorithmException, IOException {
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("parkId", amusementParkId);
		String parkPlansStr = HttpUtil.doPostJson(yeepayParkPlansUrl, paramsMap,
				HttpUtil.createBaseHeader(yeepayAccessKeyId, yeepayAccessKeySecret));
		JSONObject parkPlansJson = JSONObject.parseObject(parkPlansStr);
		ValidateUtil.validateRemoteCall("park_plans", parkPlansJson, EnumHandle.CALL_PARK_PLANS_ERROR);
		JSONArray parkPlansValueJsonArray = parkPlansJson.getJSONArray("value");
		// 调用游乐园接口，获取分账详情
		JSONArray result = new JSONArray();
		JSONObject parkPlansValueJson = null;
		for (Object object : parkPlansValueJsonArray) {
			parkPlansValueJson = JSONObject.parseObject(object.toString());
			if (parkPlansValueJson == null) {
				throw new CustomException(EnumHandle.CALL_PARK_PLANS_RESULT_ERROR);
			}
			JSONObject divideJson = new JSONObject();
			divideJson.put("ledgerNo", parkPlansValueJson.getString("ledgerNo"));
			divideJson.put("ledgerName", parkPlansValueJson.getString("ledgerName"));
			if (parkPlansValueJson.getString("amount") != null) {
				if (new BigDecimal(parkPlansValueJson.getString("amount")).doubleValue() < ConstUtil.MIN_AMOUT) {
					throw new CustomException(EnumHandle.LEDGER_AMOUNT_ERROR);
				}
				divideJson.put("amount", parkPlansValueJson.getString("amount"));
			} else if (parkPlansValueJson.getString("proportion") != null) {
				BigDecimal proportion = new BigDecimal(parkPlansValueJson.getString("proportion"));
				BigDecimal amount = new BigDecimal(actualAmount).multiply(proportion);
				if (amount.doubleValue() < ConstUtil.MIN_AMOUT) {
					throw new CustomException(EnumHandle.LEDGER_AMOUNT_ERROR);
				}
				divideJson.put("proportion", parkPlansValueJson.getString("proportion"));
			} else {
				throw new CustomException(EnumHandle.CALL_PARK_PLANS_RESULT_ERROR);
			}
			result.add(divideJson);
		}
		return result;
	}

	/**
	 * 聚合api收银台
	 * 
	 * @param tradeOrderJson
	 * @return JSONObject
	 * @throws IOException
	 */
	private JSONObject yeepayApiPay(JSONObject tradeOrderJson, Map<String, Object> params)
			throws CustomException, IOException, Exception {
		JSONObject result = new JSONObject();
		YopRequest request = new YopRequest(yeepayAppKey, yeepaySioeyePrivateKey);
		request.addParam("payTool", EnumPayTool.MINI_PROGRAM.toString());
		request.addParam("payType", EnumPayType.WECHAT.toString());
		request.addParam("token", tradeOrderJson.getString("token"));
		request.addParam("appId", weixinAppId);
		request.addParam("openId", params.get("openId").toString());
		request.addParam("version", ConstUtil.YEEPAY_INTERFACE_VERSION);
		request.addParam("userIp", params.get("spbillCreateIp").toString());
		// 获取拓展参数
		JSONObject extParamMap = new JSONObject();
		extParamMap.put("reportFee", EnumReportFee.XIANXIA.toString());
		request.addParam("extParamMap", extParamMap);
		YopResponse response = YopRsaClient.post(yeepayApiPayUrl, request);
		// 返回结果
		JSONObject yeepayJson = JSONObject.parseObject(response.getStringResult());
		if (!response.isSuccess() || !ConstUtil.YEEPAY_API_PAY_RETURN_SUCCESS.equals(yeepayJson.getString("code"))) {
			YopError yopError = response.getError();
			EnumHandle.YEEPAY_API_PAY_FAILURE.setCode(yopError.getCode());
			EnumHandle.YEEPAY_API_PAY_FAILURE.setMessage(yopError.getMessage());
			throw new CustomException(EnumHandle.YEEPAY_API_PAY_FAILURE);
		}
		result.put("resultType", yeepayJson.getString("resultType"));
		result.put("resultData", yeepayJson.getString("resultData"));
		return result;
	}

	/**
	 * 保存易宝账号
	 * 
	 * @return int
	 * @throws Exception
	 * @throws CustomException
	 */
	private void saveYeepay(Yeepay yeepay, Map<String, Object> params, JSONObject tradeOrderJson)
			throws CustomException, Exception {
		if (yeepay == null) {
			yeepay = new Yeepay();
			this.insertYeepay(yeepay, tradeOrderJson, params);
			if (iYeepayDao.save(yeepay) < ConstUtil.SAVE_UPDATE_DB_SUCCESS) {
				throw new CustomException(EnumHandle.SAVE_YEEPAY_FAILURE);
			}
		} else {
			this.insertYeepay(yeepay, tradeOrderJson, params);
			if (iYeepayDao.update(yeepay) < ConstUtil.SAVE_UPDATE_DB_SUCCESS) {
				throw new CustomException(EnumHandle.SAVE_YEEPAY_FAILURE);
			}
		}
		// 删除已有数据
		iLedgerDetailDao.removeLedgerDetailList(yeepay.getObjectId());
		// 获取分账信息列表
		JSONArray ledgerDetailJsonArray = tradeOrderJson.getJSONArray("divideDetail");
		if (ledgerDetailJsonArray == null || ledgerDetailJsonArray.size() == 0) {
			return;
		}
		// 循环插入
		JSONObject ledgerDetailJson = null;
		LedgerDetail ledgerDetail = null;
		for (Object object : ledgerDetailJsonArray) {
			ledgerDetailJson = JSONObject.parseObject(object.toString());
			ledgerDetail = new LedgerDetail();
			this.insertLedgerDetail(ledgerDetail, params.get("actualAmount").toString(), ledgerDetailJson,
					yeepay.getObjectId());
			if (iLedgerDetailDao.save(ledgerDetail) < ConstUtil.SAVE_UPDATE_DB_SUCCESS) {
				throw new CustomException(EnumHandle.SAVE_LEDGERDETAIL_FAILURE);
			}
		}
	}

	private void insertLedgerDetail(LedgerDetail ledgerDetail, String actualAmount, JSONObject ledgerDetailJson,
			String yeepayId) {
		ledgerDetail.setAmount(ledgerDetailJson.getBigDecimal("amount"));
		ledgerDetail.setProportion(ledgerDetailJson.getBigDecimal("proportion"));
		ledgerDetail.setLedgerName(ledgerDetailJson.getString("ledgerName"));
		ledgerDetail.setLedgerNo(ledgerDetailJson.getString("ledgerNo"));
		ledgerDetail.setYeepayId(yeepayId);
		if (ledgerDetail.getAmount() != null) {
			ledgerDetail.setLedgerType(EnumLedgerType.AMOUNT.getCode());
		} else if (ledgerDetail.getProportion() != null) {
			// 计算出分赃金额
			BigDecimal amount = new BigDecimal(actualAmount);
			BigDecimal proportion = ledgerDetail.getProportion();
			BigDecimal ledgerAmount = amount.multiply(proportion).setScale(2, RoundingMode.FLOOR);
			if (ledgerAmount.doubleValue() < ConstUtil.MIN_AMOUT) {
				throw new CustomException(EnumHandle.LEDGER_AMOUNT_ERROR);
			} else {
				ledgerDetail.setAmount(ledgerAmount);
			}
			ledgerDetail.setLedgerType(EnumLedgerType.PROPORTION.getCode());
		} else {
			throw new CustomException(EnumHandle.LEDGER_TYPE_ERROR);
		}
	}

	private void insertYeepay(Yeepay yeepay, JSONObject tradeOrderJson, Map<String, Object> params) {
		yeepay.setCreateTime(new Date());
		if (tradeOrderJson.getString("fundProcessType") != null
				&& !"".equals(tradeOrderJson.getString("fundProcessType"))) {
			yeepay.setFundProcessType(tradeOrderJson.getString("fundProcessType"));
		} else {
			yeepay.setFundProcessType(EnumFundProcessType.REAL_TIME.toString());
		}
		yeepay.setGoodsParamExt(tradeOrderJson.getJSONObject("goodsParamExt").toJSONString());
		yeepay.setMerchantNo(yeepayMerchantNo);
		yeepay.setToken(tradeOrderJson.getString("token"));
		yeepay.setParentMerchantNo(yeepayMerchantParentNo);
		yeepay.setPaymentId(params.get("paymentId").toString());
		yeepay.setUniqueOrderNo(tradeOrderJson.getString("uniqueOrderNo"));
	}

	/**
	 * 封装返回值信息
	 * 
	 * @return JSONObject
	 */
	private JSONObject packagePreYeepay(JSONObject tradeOrderJson, JSONObject apiPayJson) {
		JSONObject result = new JSONObject();
		if (apiPayJson == null) {
			throw new CustomException(EnumHandle.TRADE_ORDER_PREPAYID_ERROR);
		}
		result.put("resultData", apiPayJson.getJSONObject("resultData"));
		result.put("resultType", apiPayJson.getString("resultType"));
		return result;
	}

	@Override
	public JSONObject getYeepayOrderDetail(Map<String, Object> params) throws CustomException, IOException, Exception {
		Optional.ofNullable(params.get("orderId"))
				.orElseThrow(() -> new CustomException(EnumHandle.PARAMS_ORDERID_ERROR));
		Optional.ofNullable(params.get("paymentId"))
				.orElseThrow(() -> new CustomException(EnumHandle.PARAMS_PAYMENTID_ERROR));
		// 查询订单详情
		String orderId = params.get("orderId").toString();
		String paymentId = params.get("paymentId").toString();
		return this.getYeepayOrderDetail(orderId, paymentId);
	}

	/**
	 * 查询订单详情
	 * 
	 * @param tradeOrderJson
	 * @return JSONObject
	 * @throws IOException
	 */
	private JSONObject getYeepayOrderDetail(String orderId, String paymentId)
			throws CustomException, IOException, Exception {
		JSONObject result = new JSONObject();
		// 查询出yeepay
		Yeepay yeepay = iYeepayDao.getYeepayByPaymentId(paymentId);
		if (yeepay == null) {
			throw new CustomException(EnumHandle.YEEPAY_NOT_EXIST);
		}
		YopRequest request = new YopRequest(yeepayAppKey, yeepaySioeyePrivateKey);
		request.addParam("parentMerchantNo", yeepayMerchantParentNo);
		request.addParam("merchantNo", yeepayMerchantNo);
		request.addParam("orderId", orderId);
		request.addParam("uniqueOrderNo", yeepay.getUniqueOrderNo());
		// 获取拓展参数
		YopResponse response = YopRsaClient.post(yeepayQueryOrderUrl, request);
		// 返回结果
		JSONObject yeepayJson = JSONObject.parseObject(response.getStringResult());
		if (!response.isSuccess() || !ConstUtil.YEEPAY_API_RETURN_SUCCESS.equals(yeepayJson.getString("code"))) {
			YopError yopError = response.getError();
			EnumHandle.YEEPAY_QUERY_ORDER_FAILURE.setCode(yopError.getCode());
			EnumHandle.YEEPAY_QUERY_ORDER_FAILURE.setMessage(yopError.getMessage());
			throw new CustomException(EnumHandle.YEEPAY_QUERY_ORDER_FAILURE);
		}
		result.put("tradeState", yeepayJson.getString("status"));
		result.put("tradeStateDesc", yeepayJson.getString("status"));
		return result;
	}
}
