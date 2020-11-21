package com.sioeye.youle.run.order.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sioeye.youle.run.order.application.IGoodsOrderAppService;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.context.BaseResponse;
import com.sioeye.youle.run.order.context.ErrorResponse;
import com.sioeye.youle.run.order.context.SuccessResponse;
import com.sioeye.youle.run.order.service.intf.IOrderBase;
import com.sioeye.youle.run.order.util.LogUtil;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/order")
@Log4j
public class OrderController {

	private static final Log logger = LogFactory.getLog(OrderController.class);
	@Autowired
	private IOrderBase iOrderBase;

	@Autowired
	private IGoodsOrderAppService goodsOrderAppService;

	/**
	 * @api {POST} /order/presentright 获取用户在指定游乐园是否享受赠送权益
	 * @apiName presentright
	 * @apiGroup ord er
	 * @apiVersion 0.0.1
	 * @apiDescription 获取用户在指定游乐园是否享受赠送权益
	 * @apiParam {String} amusementParkId 游乐园id
	 * @apiParamExample {json} 获取用户在指定游乐园是否享受赠送权益: { "amusementParkId":
	 *                  "7ffdef467efa4b12946b56309b57f48e" //游乐园id }
	 * @apiHeaderExample {json} 获取用户在指定游乐园是否享受赠送权益url接口presentright请求参数头信息:
	 *                   {"x_youle_type": 1, "x_youle_appid":"
	 *                   548b7e55e809a8eeaec18ededb29b659","x_youle_appsignkey":
	 *                   "a61f5862612a35d55925ea58b79954f8,1539925961439",
	 *                   "Content-Type": "application/json",
	 *                   "x_youle_sessiontoken":"
	 *                   UVhJc3ZkUExzc2czU1g1VkF0MnZ3bUpSSEJROUxOK1hCRkx4a1VnZFRPdmk1UHc2VWdLVzNmWklSN2NBaUVPbmVVWHdmWjQ1dGxOc3BTM0pTbEpmZjN1S3c2dU95azFhUUU4b25GWE9qUnpTTWhSNnhUZlFIRzlVcW5GWjVzNmFUeXlScjJQdUozaVEvRDZUdSszcFRrN1BwOXlXN05UU3hQTW81OG9KeGRXbHJuVFBJT0JWNHJwNUYyMkJSazk2UnFpLzZHV1dHME1hOUNXa0FjVlA3a2x1QlRPcFB5YXFKUnNFVzFXM1pERmdBaVBTbXhUSlpJeG80SURPQXJ2a2VhT1hiWC9reXpBYU5XK2dLQnJOaml4VWw4Wk1OWmk3aXhCRm5UNXBqQ0toOWd4QWlwczNQdHR5Tkk3TWo1TjNYTXBxLzRUSzlxU1JjQnA1eFNmVXAraFI4TDhOb3VOS0NQcnJHbjVuZlJ3ZkhxSW5PbXhjTWpCMTVHMklCSVRFOTVhSFBlampEeVZ3clEvT2JJZEpQWThYKzkwZGVoSDBWaDc2Wjc4M2RXQ0hTa1ZxYXl3Mk5HTFc1Y3MyakdXV29zbGNVajhQQy8xcGt1dlVoekd5bmFVeTF1MFVIalJJaU1SZTVGOWQ5Kzl1QUl0UVVkMVhuYjNuRXBERWFLbVdQbUp6Q1hQZFJ2b2xQbTBNVmFjU1NUYU4ybGY3clJtUUhyay9mWjgwL1pvMmRDVkZubnlKdmovVXQyTGVhRElXdzRWMXZqL1FHVkJZU2xqNmJXSTNRcTdFczBtMDEvbTVBb05zeThTUUpITWZ4L2o1ZjFhZWo3Y1NTVXNHWDRZQlVKWExYc0RJZjNzc2F0TVFLMWQwbHVtNW4yRVVwODJDR0VpNnlBd0lQQVU9___1554776249541___5C695C9C7CC84B0DA5A6B534ADEF1202___6"}
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 生成成功返回数据
	 * @apiSuccessExample {json} 获取用户在指定游乐园是否享受赠送权益url接口presentright 成功json返回值:
	 *                    { "success": true, "value": [ { "goodsType": 3, //商品类型
	 *                    "usePresent": true, //是否能使用赠送 "goodsIds": [ //已购商品id列表
	 *                    "cdb8e008fac311e9a68706475c1589f2",
	 *                    "9acfcc1b241d42118e51e6cb53df9643",
	 *                    "de6f3ac8fee211e9937b06475c1589f2",
	 *                    "a4aec73205714ce9ba451755d1ac8011" ] } ] }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiErrorExample {json} 获取用户在指定游乐园是否享受赠送权益url接口presentright失败错误码:
	 *                  {"110500":"服务器内部错误","110526":"param userId is
	 *                  incorrect","110513":"place order params amusementParkId
	 *                  is incorrect . "}
	 */
	/**
	 * 获取用户在指定游乐园的赠送权益
	 * 
	 * @param params
	 * @param userId
	 * @return
	 */
	@PostMapping(value = "/presentright")
	public String getPresentRight(@RequestBody Map<String, String> params,
			@RequestParam(value = "userId") String userId) {
		Optional.ofNullable(params.get("amusementParkId"))
				.orElseThrow(() -> new CustomException(EnumHandle.PLACE_ORDER_PARKID_PARAM_INCORRECT));
		BaseResponse baseResponse = null;
		try {
			baseResponse = SuccessResponse
					.build(goodsOrderAppService.getPersonalPresentRight(userId, params.get("amusementParkId")));
		} catch (CustomException e) {
			log.error("{\"url\":\"getPresent\"," + "\"error\":\"" + e.getCode() + "\"," + "\"code\":\"" + e.getMessage()
					+ "\"}");
			baseResponse = ErrorResponse.build(e.getCode(), e.getMessage());
		} catch (Exception e) {
			log.error("{\"url\":\"getPresent\"," + "\"error\":\"" + EnumHandle.INTERNAL_ERROR.getCode() + "\","
					+ "\"code\":\"" + e.getMessage() + "\"}");
			baseResponse = ErrorResponse.build(EnumHandle.INTERNAL_ERROR.getCode(), e.getMessage());
		}
		return JSONObject.toJSONString(baseResponse, SerializerFeature.PrettyFormat,
				SerializerFeature.NotWriteDefaultValue);
	}

	/**
	 * @api {POST} /order/get_order_resource 查询订单资源列表(打印1+1 ,小程序个人已购中心)
	 * @apiName get_order_resource
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 查询订单资源列表
	 * @apiParam {json} orderId 订单id
	 * @apiParamExample {json} 查询订单资源列表:
	 *                  {"orderId":"1147vpw6zzmog4ovi1xxyyxgxxcy"}
	 * @apiHeaderExample {json} 查询打印照片的url接口get_order_resource请求参数头信息:
	 *                   {"x_youle_type": 1, "x_youle_appid":"
	 *                   548b7e55e809a8eeaec18ededb29b659","x_youle_appsignkey":
	 *                   "a61f5862612a35d55925ea58b79954f8,1539925961439",
	 *                   "Content-Type": "application/json",
	 *                   "x_youle_sessiontoken":"
	 *                   UVhJc3ZkUExzc2czU1g1VkF0MnZ3bUpSSEJROUxOK1hCRkx4a1VnZFRPdmk1UHc2VWdLVzNmWklSN2NBaUVPbmVVWHdmWjQ1dGxOc3BTM0pTbEpmZjN1S3c2dU95azFhUUU4b25GWE9qUnpTTWhSNnhUZlFIRzlVcW5GWjVzNmFUeXlScjJQdUozaVEvRDZUdSszcFRrN1BwOXlXN05UU3hQTW81OG9KeGRXbHJuVFBJT0JWNHJwNUYyMkJSazk2UnFpLzZHV1dHME1hOUNXa0FjVlA3a2x1QlRPcFB5YXFKUnNFVzFXM1pERmdBaVBTbXhUSlpJeG80SURPQXJ2a2VhT1hiWC9reXpBYU5XK2dLQnJOaml4VWw4Wk1OWmk3aXhCRm5UNXBqQ0toOWd4QWlwczNQdHR5Tkk3TWo1TjNYTXBxLzRUSzlxU1JjQnA1eFNmVXAraFI4TDhOb3VOS0NQcnJHbjVuZlJ3ZkhxSW5PbXhjTWpCMTVHMklCSVRFOTVhSFBlampEeVZ3clEvT2JJZEpQWThYKzkwZGVoSDBWaDc2Wjc4M2RXQ0hTa1ZxYXl3Mk5HTFc1Y3MyakdXV29zbGNVajhQQy8xcGt1dlVoekd5bmFVeTF1MFVIalJJaU1SZTVGOWQ5Kzl1QUl0UVVkMVhuYjNuRXBERWFLbVdQbUp6Q1hQZFJ2b2xQbTBNVmFjU1NUYU4ybGY3clJtUUhyay9mWjgwL1pvMmRDVkZubnlKdmovVXQyTGVhRElXdzRWMXZqL1FHVkJZU2xqNmJXSTNRcTdFczBtMDEvbTVBb05zeThTUUpITWZ4L2o1ZjFhZWo3Y1NTVXNHWDRZQlVKWExYc0RJZjNzc2F0TVFLMWQwbHVtNW4yRVVwODJDR0VpNnlBd0lQQVU9___1554776249541___5C695C9C7CC84B0DA5A6B534ADEF1202___6"}
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 生成成功返回数据
	 * @apiSuccessExample {json} 查询打印照片的url接口get_order_resource接口成功json返回值:
	 *                    {"success": true, "value": { "actualAmount": 0.00,
	 *                    //订单实际金额 "currency": "CNY", //币种 "orderId":
	 *                    "1147vpw6zzmog4ovi1xxyyxgxxcy", //订单id "orderItem": [
	 *                    { "actualAmount": 0.00, //商品实际金额 "count": 1, //商品数量
	 *                    "createDate": 1570691942120, //订单项创建日期 "goodsId":
	 *                    "xmhkxi1uzkc3sgtakpz4vcyp07bjxjrv", //商品id
	 *                    "orderItemId": "a04bc6721a704e3893489f3eb41dc450",
	 *                    //订单项id "price": 0.00, //商品单价 "type": 6 //商品类型 }, {
	 *                    "actualAmount": 0.00, "count": 1, "createDate":
	 *                    1570691942120, "downloadUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/images/61a0fd29da184a6d8734cf9526472eaa.jpg?x-amz-acl=public-read&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20191010T072841Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1799&X-Amz-Credential=AKIAO7QXSMTQKKQGQWRQ%2F20191010%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Signature=f4be4771822049e2c530dad6afe5387863ff1f737c97cbce9a394c954796eb7e",
	 *                    "goodsId": "61a0fd29da184a6d8734cf9526472eaa",
	 *                    "orderItemId": "aafea5f8aaaa4b71b71127ebd5bcd4d6",
	 *                    "previewUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/images/61a0fd29da184a6d8734cf9526472eaa_preview.jpg",
	 *                    "price": 0.00, "type": 5 }, { "actualAmount": 0.00,
	 *                    "count": 1, "createDate": 1570691942120,
	 *                    "downloadUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/images/61a0fd29da184a6d8734cf9526472eaa.jpg?x-amz-acl=public-read&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20191010T072841Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1800&X-Amz-Credential=AKIAO7QXSMTQKKQGQWRQ%2F20191010%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Signature=acc5fc14b975325b780cb2fd58287cc863878906947618f9c51973d31a2a683c",
	 *                    "goodsId": "61a0fd29da184a6d8734cf9526472eaa",
	 *                    "orderItemId": "09611b662a4c47049da57cfa90197777",
	 *                    "previewUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/images/61a0fd29da184a6d8734cf9526472eaa_preview.jpg",
	 *                    "price": 0.00, "type": 3 }, { "actualAmount": 0.00,
	 *                    "count": 1, "createDate": 1570691942120,
	 *                    "downloadUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/videos/241ba46d70d64698ad807841ccadbc24.mp4?x-amz-acl=public-read&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20191010T072841Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1800&X-Amz-Credential=AKIAO7QXSMTQKKQGQWRQ%2F20191010%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Signature=49076a7bd844cec2265e56d402aa369dd4bfc09ccd51728ed10f2d7b171ff93a",
	 *                    "goodsId": "241ba46d70d64698ad807841ccadbc24",
	 *                    "orderItemId": "e141670ad88a48f1973464c61998bb5a",
	 *                    "previewUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/videos/241ba46d70d64698ad807841ccadbc24_preview.mp4",
	 *                    "price": 1.00, "thumbnailUrl":
	 *                    "https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/origin/a7c0402edfc04db5840b75b5e1948da6/b8f902febc134da88a5418bfaa78112c/a22ab3c2b6364e06851953d4e1ef85f0/images/17cddff461e94f90b9af240c7c3a665c.jpg",
	 *                    "type": 0 } ], "orderStatus": 1, //订单状态
	 *                    "originalAmount": 1.00, //订单原始金额 "payWay": 7, //订单支付方式
	 *                    "placeOrderDate": 1570691942120, //下单日期 "paymentDate":
	 *                    1570691942120, //支付日期 "userId":
	 *                    "5C695C9C7CC84B0DA5A6B534ADEF1202" //用户id } }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiErrorExample {json} 查询打印照片的url接口get_order_resource失败错误码:
	 *                  {"110500":"服务器内部错误","110505":"查询订单参数错误","110507":"调用购买视频服务查询列表失败"}
	 */
	@PostMapping(value = "/get_order_resource")
	public String getOrderResource(@RequestBody Map<String, String> params,
			@RequestParam(value = "userId") String userId) {
		BaseResponse baseResponse = null;
		try {
			baseResponse = SuccessResponse
					.build(goodsOrderAppService.getOrderResourceByUserId(params.get("orderId"), userId));
		} catch (CustomException e) {
			log.error("{\"url\":\"get_order_resource\"," + "\"error\":\"" + e.getCode() + "\"," + "\"code\":\""
					+ e.getMessage() + "\"}");
			baseResponse = ErrorResponse.build(e.getCode(), e.getMessage());
		} catch (Exception e) {
			log.error("{\"url\":\"get_order_resource\"," + "\"error\":\"" + EnumHandle.INTERNAL_ERROR.getCode() + "\","
					+ "\"code\":\"" + e.getMessage() + "\"}");
			baseResponse = ErrorResponse.build(EnumHandle.INTERNAL_ERROR.getCode(), e.getMessage());
		}
		return JSONObject.toJSONString(baseResponse, SerializerFeature.PrettyFormat,
				SerializerFeature.NotWriteDefaultValue);
	}

	/**
	 * @api {POST} /order/place_order 生成订单
	 * @apiName place_order
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 生成订单
	 * @apiParam {String} openId 小程序用户openId
	 * @apiParam {String} amusementParkId 游乐园id
	 * @apiParam {Number} actualAmount 支付价格
	 * @apiParam {json[]} goodsList 商品id列表,格式为Json数组:[ { "goodsId":"xxx",
	 *           "goodsType":5,(4:套餐,5：照片打印,6:打印1+1)
	 *           "resourceType":1,(1:机位视频,2:小组视频,3:项目视频,4:传统集锦,5:攀爬集锦,6:机位镜像视频,7:小组镜像视频,
	 *           8:项目镜像视频,9:传统镜像集锦,10:攀爬镜像集锦,11:定点照片,12:摆拍照片,13:单反摆拍照片,
	 *           14:绿幕照片,15:定点镜像照片,16:摆拍镜像照片,17:单反摆拍镜像照片,18:绿幕镜像照片)
	 *           "resourceCategory":0,(0:视频，1:照片)
	 *           //goodsType和resourceType、resourceCategory二选一 } ]
	 * @apiParam {String} deviceRecordId 设备记录id
	 * @apiParam {String} [deviceType] 设备类型，0 小程序(默认)，1 H5,2 TV，3 广告机,4 通行证
	 * @apiParam {String} [combineSearchId] 搜索id
	 * @apiParam {Number} [payWay] 支付方式 (默认为7)
	 * @apiParam {Number} [discountType] 优惠方式(0原价(默认),1普通优惠,2套票,3分享活动,4赠送)
	 * @apiParam {String} [sceneId] 场景id(H5下单必传)
	 * @apiParam {String} [parkShareActivityId] 游乐园分享活动id(优惠方式为3时才需要传)
	 * @apiParam {String} [formId] 微信表单id(可选)
	 * @apiHeaderExample {json} 生成订单接口(place_orde)接口url请求参数header信息json请求值:
	 *                   {"Content-Type": "application/json", "x_youle_appid":
	 *                   "548b7e55e809a8eeaec18ededb29b659s",
	 *                   "x_youle_appsignkey":
	 *                   "9c6bf13d69c5fd7f867145ee7b67a366,1537254000000"
	 *                   "x_youle_sessiontoken":
	 *                   "Z1hqNFdnTkJKeU9FcmdJVHAzMC9rN1R3eXJWTXNRb",
	 *                   "x_youle_type": 1 }
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 生成成功返回数据
	 * @apiSuccessExample {json} 生成订单接口(place_orde)接口请求url成功后的json返回值:
	 *                    {"success": true, "value":{ "orderId":
	 *                    "31884DBEE2904C4D82CA14AA94DDFF6F", "openId":
	 *                    "ovZTu0CdanBgGmSDb--fIwkIN-v0", "actualAmount": 18.0,
	 *                    "sign": "C0A104F52957E19C33E90A957E22037F",
	 *                    "updateTime": 1528881118303, "count": 2, "activityId":
	 *                    "dd70e7e412504b0ea36d13b219a6d391", "orderTime":
	 *                    1528881119591, "originalAmount": 18.0, "appId":
	 *                    "wx0f5ea6495bac0d7d", "amusementParkId":
	 *                    "W2E3E3EU239UED9QD3IE320IE032D321", "resultDate": {
	 *                    "timeStamp": "1572230942", "package":
	 *                    "prepay_id=disney_free_payment", "paySign":
	 *                    "BC465DE1D81D710DB56CD8011F15E3BC", "appId":
	 *                    "wxf111835d516611c9", "signType": "MD5", "nonceStr":
	 *                    "thBM3TtEpA8nRhmE" } "tradeType":"MWEB_DISNEY" } }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 生成订单接口Place_orde请求失败后的错误码:
	 *                  {"110500":"服务器内部错误","110501":"订单保存失败","110502":"交易类型错误","110503":"调用支付服务失败","110504":"创建订单参数错误",
	 *                  "110507":"调用视频片段处理服务失败","110509":"订单价格不正确","110510":"发送购买视频队列失败",
	 *                  "110513":"游乐园id不正确","110514":"sessionToken不正确","110515":"活动id不正确",
	 *                  "110516":"视频片段不正确","110517":"视频片段数量不正确","110518":"视频原价不正确",
	 *                  "110519":"实际支付价格不正确","110520":"支付方式不正确","110521":"微信openId不正确"
	 *                  "110522":"获取游乐园价格错误","110523":"用户正在创建订单","110525":"获取游乐园优惠时间错误"}
	 */
	/**
	 * @api {POST} /deviceorder/order/place_order 生成订单
	 * @apiName place_order
	 * @apiGroup deviceorder
	 * @apiVersion 0.0.1
	 * @apiDescription 生成订单
	 * @apiParam {Number} actualAmount 支付价格
	 * @apiParam {String} amusementParkId 游乐园id
	 * @apiParam {String} deviceRecordId device设备id
	 * @apiParam {String} [deviceType] 设备类型，0 小程序(默认)，1 H5,2 TV，3 广告机，4 通行证
	 * @apiParam {Number} discountType 优惠方式=2套餐
	 * @apiParam {json[]} goodsList 商品id列表,格式为Json数组:[ { "goodsId":"xxx",
	 *           "goodsType":5,(4:套餐,5：照片打印,6:打印1+1)
	 *           "resourceType":1,(1:机位视频,2:小组视频,3:项目视频,4:传统集锦,5:攀爬集锦,6:机位镜像视频,7:小组镜像视频,
	 *           8:项目镜像视频,9:传统镜像集锦,10:攀爬镜像集锦,11:定点照片,12:摆拍照片,13:单反摆拍照片,
	 *           14:绿幕照片,15:定点镜像照片,16:摆拍镜像照片,17:单反摆拍镜像照片,18:绿幕镜像照片)
	 *           "resourceCategory":0,(0:视频，1:照片)
	 *           //goodsType和resourceType、resourceCategory二选一 } ]
	 * @apiHeaderExample {json} 生成订单接口(place_orde)请求url的参数header信息json值:
	 *                   {"Content-Type":"application/json", "x_youle_appid":
	 *                   "548b7e55e809a8eeaec18ededb29b659s",
	 *                   "x_youle_appsignkey":
	 *                   "9c6bf13d69c5fd7f867145ee7b67a366,1537254000000"
	 *                   "x_youle_sessiontoken":
	 *                   "Z1hqNFdnTkJKeU9FcmdJVHAzMC9rN1R3eXJWTXNRb",
	 *                   "x_youle_type": 1 }
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 生成成功返回数据
	 * @apiSuccessExample {json} 生成订单接口(place_orde)请求url成功后的json返回值:
	 *                    {"success":true, "value":{ "orderId":
	 *                    "31884DBEE2904C4D82CA14AA94DDFF6F", "openId":
	 *                    "ovZTu0CdanBgGmSDb--fIwkIN-v0", "actualAmount": 18.0,
	 *                    "sign": "C0A104F52957E19C33E90A957E22037F",
	 *                    "updateTime": 1528881118303, "count": 2, "activityId":
	 *                    "dd70e7e412504b0ea36d13b219a6d391", "orderTime":
	 *                    1528881119591, "originalAmount": 18.0, "appId":
	 *                    "wx0f5ea6495bac0d7d", "amusementParkId":
	 *                    "W2E3E3EU239UED9QD3IE320IE032D321", "resultDate": {
	 *                    "timeStamp": "1572230942", "package":
	 *                    "prepay_id=disney_free_payment", "paySign":
	 *                    "BC465DE1D81D710DB56CD8011F15E3BC", "appId":
	 *                    "wxf111835d516611c9", "signType": "MD5", "nonceStr":
	 *                    "thBM3TtEpA8nRhmE" } "tradeType":"MWEB_DISNEY" } }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 生成订单接口Place_orde请求失败后的错误码:
	 *                  {"110500":"服务器内部错误","110501":"订单保存失败","110502":"交易类型错误","110503":"调用支付服务失败","110504":"创建订单参数错误",
	 *                  "110507":"调用视频片段处理服务失败","110509":"订单价格不正确","110510":"发送购买视频队列失败",
	 *                  "110513":"游乐园id不正确","110514":"sessionToken不正确","110515":"活动id不正确",
	 *                  "110516":"视频片段不正确","110517":"视频片段数量不正确","110518":"视频原价不正确",
	 *                  "110519":"实际支付价格不正确","110520":"支付方式不正确","110521":"微信openId不正确"
	 *                  "110522":"获取游乐园价格错误","110523":"用户正在创建订单","110525":"获取游乐园优惠时间错误"}
	 */
	@RequestMapping(value = "/place_order", method = RequestMethod.POST)
	public String placeOrder(@RequestBody Map<String, Object> params, @RequestParam(value = "userId") String userId,
			@RequestParam(value = "realIP") String realIP) {
		JSONObject result = new JSONObject();
		try {
			params.put("usersId", userId);
			params.put("spbillCreateIp", realIP);
			result = JSONObject.parseObject(LogUtil.packageSuccessLog(logger, iOrderBase.placeOrder(params)));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, params);
			result = LogUtil.BusinessError(logger,
					new CustomException(EnumHandle.INTERNAL_ERROR.getCode(), e.getMessage()));
		}
		return result.toString();
	}

	/**
	 * @api {POST} /order/display 用户点击小视频播放页面、照片浏览页面、套餐详情页面
	 * @apiName display
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 查询照片或小视频订单详情
	 * @apiParam {String} goodsId 资源id或者商品id
	 * @apiParam {Integer} resourceType 资源类型
	 * @apiParam {Integer} resourceCategory 资源种类0：视频1：照片
	 * @apiParam {String} amusementParkId 游乐园id
	 * @apiParam {Integer} [goodsType] 商品类型
	 * @apiHeaderExample {json} 商品/资源展示接口/order/display请求url的参数头信息Json数据:
	 *                   {"Content-Type":"application/json",
	 *                   "x_youle_appid":"548b7e55e809a8eeaec18ededb29b659s",
	 *                   "x_youle_appsignkey":"9c6bf13d69c5fd7f867145ee7b67a366,1537254000000"
	 *                   "x_youle_sessiontoken":"Z1hqNFdnTkJKeU9FcmdJVHAzMC9rN1R3eXJWTXNRb",
	 *                   "x_youle_type":1, "x_youle_flag":1 }
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccessExample {json} 用户点击视频播放页面接口Url(display接口)请求成功返回Json返回值:
	 *                    {"success":true, "value":{
	 *                    "goods":"商品信息","order":"订单信息","price":{"价格基本信息":"价格基本信息","priceList":[{"sharePrice":"分享价格信息(数组)"},"goodsPrice":"商品价格信息","couponPrice":"套餐信息"]}}
	 *                    }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 用户点击视频播放页面接口(display)请求失败返回Json返回:
	 *                  {"110500":"服务器内部错误", "110505":"参数错误", "110509":"价格错误",
	 *                  "110544":"获取视频信息错误", "110552":"clipid不正确",
	 *                  "110533":"获取集锦信息错误", "110555":"获取照片信息错误",
	 *                  "110559":"获取照片url错误" }
	 */
	/**
	 * @api {POST} /deviceorder/order/display 照片打印页面详情(H5桌面终端调用)
	 * @apiName display
	 * @apiGroup deviceorder
	 * @apiVersion 0.0.1
	 * @apiDescription 查询照片打印订单详情
	 * @apiParam {String} goodsId 资源id或者商品id
	 * @apiParam {Integer} resourceType 资源类型
	 * @apiParam {Integer} resourceCategory 资源种类0：视频1：照片
	 * @apiParam {String} amusementParkId 游乐园id
	 * @apiParam {Integer} [goodsType] 商品类型
	 * @apiHeaderExample {json} {json}
	 *                   商品/资源展示接口/order/display请求url请求参数头信息Json数据:
	 *                   {"Content-Type":"application/json",
	 *                   "x_youle_appid":"548b7e55e809a8eeaec18ededb29b659s",
	 *                   "x_youle_appsignkey":"9c6bf13d69c5fd7f867145ee7b67a366,1537254000000"
	 *                   "x_youle_devicetoken":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJkdWVEYXRlIjoxNTY4Mjc0OTE3NDI4LCJjbGllbnRJcCI6IjE4Mi4xNTAuMjQuNDYiLCJuYW1lIjoi5p2O5L2zY2hyb21l5rWP6KeI5ZmoIiwib2JqZWN0SWQiOiJmNDhmYWZiZDRmNzA0YTBlOTJlM2NiYWQ3YzI2ZWMzNiIsImNyZWF0ZURhdGUiOjE1NjgyNzEzMTc0Mjh9.HqpJC1dZeN7zmGxaA3kMbMfzniUIN0KxBBH6KdGY5SOgAOD3l9f6cMfkhjtRNohtoZnTumok8NJ0JN7KXANpVzEg6M-Itl9hr72oahOeS8uSzPQA54pHLJFwCwiZUHprNHqwa_hrZu6Oy5ZeSakfIpeuzkx9KTj54FQHStctlLc",
	 *                   "x_youle_type":1, "x_youle_flag":1 }
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccessExample {json} 用户点击照片打印页面接口Url(display接口)请求成功返回Json返回值:
	 *                    {"success":true, "value":{
	 *                    "goods":"商品信息","order":"订单信息","price":{"价格基本信息":"价格基本信息","priceList":[{"sharePrice":"分享价格信息(数组)"},"goodsPrice":"商品价格信息","couponPrice":"套餐信息"]}}
	 *                    }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 用户点击照片打印页面接口Url(display接口)请求失败返回Json返回:
	 *                  {"110500":"服务器内部错误", "110505":"参数错误", "110509":"价格错误",
	 *                  "110555":"获取照片信息错误", "110559":"获取照片url错误",
	 *                  "110588":"call photoprint server is failed",
	 *                  "110563":"photo has expired, "110577":"params is not
	 *                  match", }
	 */
	@RequestMapping(value = "/display", method = RequestMethod.POST)
	public String display(@RequestBody Map<String, Object> params, @RequestParam(value = "userId") String userId) {
		BaseResponse baseResponse = null;
		try {
			params.put("userId", userId);
			baseResponse = SuccessResponse.build(iOrderBase.display(params));
		} catch (CustomException e) {
			log.error("{\"url\":\"display\"," + "\"error\":\"" + e.getCode() + "\"," + "\"code\":\"" + e.getMessage()
					+ "\"}");
			baseResponse = ErrorResponse.build(e.getCode(), e.getMessage());
		} catch (Exception e) {
			log.error("{\"url\":\"display\"," + "\"error\":\"" + EnumHandle.INTERNAL_ERROR.getCode() + "\","
					+ "\"code\":\"" + e.getMessage() + "\"}");
			baseResponse = ErrorResponse.build(EnumHandle.INTERNAL_ERROR.getCode(), e.getMessage());
		}
		return JSONObject.toJSONString(baseResponse, SerializerFeature.PrettyFormat,
				SerializerFeature.NotWriteDefaultValue);
	}

	/**
	 * 
	 * @api {POST} /order/bought 根据用户id分页查询已购商品列表(小程序调用)
	 * @apiName bought
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 小程序个人中心，根据用户id分页查询已购商品列表
	 * @apiParam {Integer} pageNo 分页页码
	 * @apiParam {Integer} pageSize 每页显示的数据条数
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {String} orderId 订单id
	 * @apiSuccess (Success) {String} amusementparkId 游乐园id
	 * @apiSuccess (Success) {String} parkName 游乐园名称
	 * @apiSuccess (Success) {String} gameName 项目名称
	 * @apiSuccess (Success) {String} goodsId
	 *             商品id(type=4的套餐不返回goodsId、thumnailUrl)
	 * @apiSuccess (Success) {Integer} type 订单商品类型---0:小视频 3:照片 4:套餐 5:照片打印
	 *             6:打印1+1
	 * @apiSuccess (Success) {Long} paymentTime 订单支付时间
	 * @apiSuccess (Success) {Integer} status 订单支付状态，1：已支付未迁移；2：已支付已迁移
	 * @apiSuccess (Success) {String} thumnailUrl 商品封面CDN预览地址
	 * @apiSuccessExample {json} 根据用户id分页查询已购商品列表bought(小程序调用)请求成功返回Json数据:
	 *                    {"success": true, "value": { "list": [ {
	 *                    "amusementparkId": "6224886b1529499ea7b1d752545f3d6d",
	 *                    "goodsId": "cbdea29a37034cb18653f28771f97272",
	 *                    "goodsName": "机位小视频", "goodsType": 0, "itemStatus": 2,
	 *                    "orderId": "53fa21dc23654fa9a39d5b3c9e209b94",
	 *                    "orderType": 0, "parkName": "125乐园", "paymentTime":
	 *                    1575270314874, "status": 1, "thumnailUrl":
	 *                    "https://pre-ylaeon.sioeye.com/6224886b1529499ea7b1d752545f3d6d/89c01a71284049dfbeb2e0ec673ffce1/images/5164c7fb02ec4fc891ab9b5b86145951.jpg"
	 *                    }, { "amusementparkId":
	 *                    "de1157a2dedc49aa986192e8717fdc22", "goodsId":
	 *                    "103eaf3c878c48fc9291897d1e8d1b7a", "goodsName":
	 *                    "机位小视频", "goodsType": 0, "itemStatus": 2, "orderId":
	 *                    "c3a4461ccc2949cc9a3c4122188ea98c", "orderType": 3,
	 *                    "parkName": "美人鱼游乐园1", "paymentTime": 1574822115259,
	 *                    "status": 1, "thumnailUrl":
	 *                    "https://pre-ylaeon.sioeye.com/de1157a2dedc49aa986192e8717fdc22/9df866bf6e5545a7933db692a3725c7a/images/b3e9c1ddfe294d8caf77f32dc60be0de.jpg"
	 *                    } ],"total": 18}}
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiErrorExample {json} 根据用户id分页查询已购商品列表bought(小程序调用)接口url请求失败返回Json数据:
	 *                  {"110500":"服务器内部错误","110526":"param userId is
	 *                  incorrect", "110594":"pageNo can't empty",
	 *                  "110595":"pageSize can't empty", }
	 * 
	 */
	@RequestMapping(value = "/bought", method = RequestMethod.POST)
	public String queryBoughtList(@RequestBody Map<String, Object> params,
			@RequestParam(value = "userId") String userId) {
		BaseResponse baseResponse = null;
		try {
			params.put("userId", userId);
			baseResponse = SuccessResponse.build(iOrderBase.queryBoughtList(params));
			LogUtil.packageSuccessLog(logger, baseResponse);
		} catch (CustomException e) {
			log.error("{\"url\":\"bought\"," + "\"error\":\"" + e.getCode() + "\"," + "\"code\":\"" + e.getMessage()
					+ "\"}");
			baseResponse = ErrorResponse.build(e.getCode(), e.getMessage());
		} catch (Exception e) {
			log.error("{\"url\":\"bought\"," + "\"error\":\"" + EnumHandle.INTERNAL_ERROR.getCode() + "\","
					+ "\"code\":\"" + e.getMessage() + "\"}");
			baseResponse = ErrorResponse.build(EnumHandle.INTERNAL_ERROR.getCode(), e.getMessage());
		}
		return JSONObject.toJSONString(baseResponse, SerializerFeature.PrettyFormat,
				SerializerFeature.NotWriteDefaultValue);
	}

	/**
	 * 
	 * @api {POST} /order/get_order_status 查询订单状态
	 * @apiName get_order_status
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 查询订单状态
	 * @apiParam {String[]} orderIdList H5打印场景号列表
	 * @apiParam {String} [deviceType] 设备类型，0 小程序,1 H5(默认),2 TV，3 广告机，4 通行证
	 * @apiSuccessExample {json} 查询订单状态(get_order_status)接口Url成功返回Json数据返回值:
	 *                    {"success":true,"value": { "orderStatus":
	 *                    -1(未下单)0(未购买)1(已购买) } }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiErrorExample {json} 查询订单状态查询订单状态接口失败返回Json数据返回值: {"110500":"服务器内部错误",
	 *                  "110505":"参数错误"}
	 */
	@RequestMapping(value = "/get_order_status", method = RequestMethod.POST)
	public String getOrderStatus(@RequestBody Map<String, Object> params,
			@RequestParam(value = "userId") String userId) {
		JSONObject result = new JSONObject();
		try {
			params.put("userId", userId);
			result = JSONObject
					.parseObject(LogUtil.packageSuccessLog(logger, goodsOrderAppService.getOrderStatus(params)));
		} catch (CustomException e) {
			result = LogUtil.BusinessError(logger, e);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, params);
			result = LogUtil.BusinessError(logger, new CustomException(EnumHandle.INTERNAL_ERROR));
		}
		return result.toString();
	}

	/**
	 * @api {POST} /order/get_order_highlighturl 查询集锦订单视频下载地址(小程序调用)[已废弃]
	 * @apiName get_order_highlighturl
	 * @apiGroup order
	 * @apiVersion 0.0.1
	 * @apiDescription 查询集锦订单视频下载地址(小程序调用)
	 * @apiParam {String} orderId 订单id
	 * @apiHeaderExample {json}
	 *                   查询集锦订单视频下载地址接口get_order_highlighturl请求参数header信息:
	 *                   {"Content-Type": "application/json", "x_youle_appid":
	 *                   "548b7e55e809a8eeaec18ededb29b659s",
	 *                   "x_youle_appsignkey":
	 *                   "9c6bf13d69c5fd7f867145ee7b67a366,1537254000000"
	 *                   "x_youle_sessiontoken":
	 *                   "Z1hqNFdnTkJKeU9FcmdJVHAzMC9rN1R3eXJWTXNRb",
	 *                   "x_youle_type": 1}
	 * @apiSuccess (Success) {Boolean} success 操作是否成功：是
	 * @apiSuccess (Success) {Object} value 查询集锦视频列表返回数据
	 * @apiSuccessExample {json} 查询集锦订单视频下载地址接口get_order_highlighturl成功json返回值:
	 *                    { "success": true, "value": { "orderId":
	 *                    "FEBD64AE835C4987AFCD5AADF74BC987", "orderStatus":
	 *                    0,"mergeDownloadUrl":
	 *                    "https://sioeye-disney-aeon-test.s3.cn-north-1.amaze",
	 *                    "usersId": "82344736AECA4855A25427C99C738B22",
	 *                    "objectId": "B1C347F8D76E4050819CB842F4790606" } }
	 * @apiError (Failure) {Boolean} success 操作是否成功：否
	 * @apiError (Failure) {String} code 错误码
	 * @apiError (Failure) {String} message 错误信息
	 * @apiError (Failure) {String} type 错误类型
	 * @apiErrorExample {json} 查询集锦订单视频下载地址接口get_order_highlighturl失败错误码:
	 *                  {"110500":"服务器内部错误","110506":"订单不存在","110534":"订单未支付","110533":"调用集锦服务失败","110713":"集锦查询参数错误","110704":"集锦不存在"}
	 * @RequestMapping(value = "/get_order_highlighturl", method =
	 *                       RequestMethod.POST) public String
	 *                       getOrderHighlightUrl(@RequestBody Map<String,
	 *                       Object> params,
	 * @RequestParam(value = "userId") String tokenUserId) { JSONObject result =
	 *                     new JSONObject(); try { result =
	 *                     JSONObject.parseObject(
	 *                     LogUtil.packageSuccessLog(logger,
	 *                     iOrderBase.getOrderHighlightUrl(tokenUserId,
	 *                     params))); } catch (CustomException e) { result =
	 *                     LogUtil.BusinessError(logger, e); } catch (Exception
	 *                     e) { result = LogUtil.BusinessError(logger, new
	 *                     CustomException(EnumHandle.INTERNAL_ERROR));
	 *                     LogUtil.internalError(logger, e, params); } return
	 *                     result.toString(); }
	 */
}
