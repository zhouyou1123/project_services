package com.sioeye.youle.run.order.util;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.context.*;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.DeviceTypeEnum;
import com.sioeye.youle.run.order.domain.order.DiscountTypeEnum;
import com.sioeye.youle.run.order.domain.order.OrderIdUtils;
import com.sioeye.youle.run.order.domain.order.PromotionTypeEnum;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderUtils {

	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static JSONObject convertOrderResponse(CreateOrderResponse response) {

		return JSONObject.parseObject(JSONObject.toJSONString(response));

	}

	public static CreateOrderRequest convertOrderRequest(Map<String, Object> params) {

		CreateOrderRequest createOrderRequest = new CreateOrderRequest();
		// 获取优惠方式
		Optional.ofNullable(params.get("discountType")).map(Object::toString).map(str -> Integer.valueOf(str))
				.ifPresent(o -> {
					createOrderRequest.setDiscountType(DiscountTypeEnum.valueOf(o));
					createOrderRequest.setPromotionType(PromotionTypeEnum.valueOf(o));
				});
		// 验证游乐园id
		createOrderRequest.setAmusementParkId(Optional.ofNullable(params.get("amusementParkId")).map(o -> o.toString())
				.filter(str -> str.length() == 32)
				.orElseThrow(() -> new CustomException(EnumHandle.PLACE_ORDER_PARKID_PARAM_INCORRECT)));
		// 计算订单实际支付金额
		createOrderRequest.setActualAmount(
				Optional.ofNullable(params.get("actualAmount")).map(Object::toString).map(str -> new BigDecimal(str))
						.orElseThrow(() -> new CustomException(EnumHandle.PLACE_ORDER_ACTUALAMOUNT_PARAM_INCORRECT)));
		// 验证支付类型
		Optional.ofNullable(params.get("payWay")).map(Object::toString).map(str -> Integer.valueOf(str))
				.map(payWay -> PayWayEnum.valueOf(payWay)).ifPresent((payWay) -> {
					createOrderRequest.setPayWay(payWay);
				});

		// 设备类型
		Optional.ofNullable(params.get("deviceType")).map(Object::toString).map(str -> Integer.valueOf(str))
				.map(deviceType -> DeviceTypeEnum.valueOf(deviceType)).ifPresent(deviceType -> {
					createOrderRequest.setDeviceType(deviceType);
				});
		// 打印设备记录id
		createOrderRequest.setDeviceRecordId(Optional.ofNullable(params.get("deviceRecordId")).map(Object::toString)
				.orElseThrow(() -> new CustomException(DomainErrorCodeEnum.PLACE_ORDER_DEVICERECORDID_ERROR.getCode(),
						DomainErrorCodeEnum.PLACE_ORDER_DEVICERECORDID_ERROR.getMessage())));

		// 验证usersId
		createOrderRequest.setUserId(
				Optional.ofNullable(params.get("usersId")).map(Object::toString).filter(userId -> userId.length() == 32)
						.orElseThrow(() -> new CustomException(EnumHandle.PLACE_ORDER_USERID_PARAM_INCORRECT)));

		// 设置支付ip
		Optional.ofNullable(params.get("spbillCreateIp")).map(Object::toString).ifPresent(ip -> {
			createOrderRequest.setSpbillCreateIp(ip);
		});
		// 设置搜索id
		createOrderRequest.setSearchId(Optional.ofNullable(params.get("combineSearchId")).map(Object::toString)
				.filter(searchId -> searchId.trim().length() > 0).orElse(OrderIdUtils.combineSearchId()));
		// 获取分享活动id
		Optional.ofNullable(params.get("parkShareActivityId")).map(Object::toString)
				.ifPresent(parkShareId -> createOrderRequest.setParkShareActivityId(parkShareId));
		Optional.ofNullable(params.get("formId")).map(Object::toString)
				.ifPresent(formId -> createOrderRequest.setFormId(formId));
		// 验证同一商品的数量不能超过两个
		createOrderRequest.setGoodsList(convertOrderGoods(params));
		createOrderRequest.getGoodsList().stream().filter(g -> g.getGoodsType() != null)
				.collect(Collectors.groupingBy(OrderGoods::goodsType)).forEach((type, goodsList) -> {
					if (goodsList.size() >= 2) {
						throw new CustomException(DomainErrorCodeEnum.GOODS_COUNT_MAX_ERROR.getCode(),
								String.format(DomainErrorCodeEnum.GOODS_COUNT_MAX_ERROR.getMessage(), type.toString()));
					}
				});
		// 验证同一个资源的数量不能超过两个
		createOrderRequest.getGoodsList().stream().filter(g -> g.getResourceType() != null)
				.collect(Collectors.groupingBy(OrderGoods::getResourceType)).forEach((type, goodsList) -> {
					if (goodsList.size() >= 2) {
						throw new CustomException(DomainErrorCodeEnum.GOODS_COUNT_MAX_ERROR.getCode(),
								String.format(DomainErrorCodeEnum.GOODS_COUNT_MAX_ERROR.getMessage(), type.toString()));
					}
				});

		// TODO 照片打印 ，用套餐密码打印 不验证openid，现在是HardCode，后续照片打印也包含在套餐范围中时需要调整
		Integer goodsType = createOrderRequest.getGoodsList().get(0).getGoodsType();
		if ((goodsType != null && !goodsType.equals(GoodsTypeEnum.PRINT.getCode()))
				|| createOrderRequest.getPromotionType().getCode() != PromotionTypeEnum.COUPON.getCode()) {
			// 验证openid
			createOrderRequest.setOpenId(Optional.ofNullable(params.get("openId")).map(Object::toString)
					.filter(openId -> StringUtils.hasText(openId))
					.orElseThrow(() -> new CustomException(EnumHandle.PLACE_ORDER_OPENID_PARAM_INCORRECT)));
		}
		// 打印id
		Optional.ofNullable(params.get("sceneId"))
				.ifPresent(sceneId -> createOrderRequest.setSceneId(sceneId.toString()));
		if (goodsType != null && (goodsType.equals(GoodsTypeEnum.PRINT1ADD1.getCode())
				|| (goodsType.equals(GoodsTypeEnum.PRINT.getCode())
						&& createOrderRequest.getPromotionType().getCode() != PromotionTypeEnum.COUPON.getCode()))
				&& !StringUtils.hasText(createOrderRequest.getSceneId())) {
			throw new CustomException(DomainErrorCodeEnum.PLACE_ORDER_SCENEID_ERROR.getCode(),
					DomainErrorCodeEnum.PLACE_ORDER_SCENEID_ERROR.getMessage());
		}
		return createOrderRequest;
	}

	private static List<OrderGoods> convertOrderGoods(Map<String, Object> params) {
		List<OrderGoods> result = null;

		Optional.ofNullable(params.get("goodsList"))
				.orElseThrow(() -> new CustomException(EnumHandle.GOODSID_NOT_EMPTY.getCode(),
						EnumHandle.GOODSID_NOT_EMPTY.getMessage()));
		List<Map<String, Object>> goodsMapList = (List<Map<String, Object>>) params.get("goodsList");
		if (goodsMapList != null && goodsMapList.size() > 0) {
			result = goodsMapList.stream().map(p -> {

				Optional.ofNullable(p.get("goodsId"))
						.orElseThrow(() -> new CustomException(EnumHandle.GOODSID_NOT_EMPTY.getCode(),
								EnumHandle.GOODSID_NOT_EMPTY.getMessage()));

				if (Optional.ofNullable(p.get("goodsType")).isPresent()) {
					return new OrderGoods(p.get("goodsId").toString(), Integer.valueOf(p.get("goodsType").toString()),
							1);
				}
				if (Optional.ofNullable(p.get("resourceType")).isPresent()
						&& Optional.ofNullable(p.get("resourceCategory")).isPresent()) {
					return new OrderGoods(p.get("goodsId").toString(),
							Integer.valueOf(p.get("resourceType").toString()),
							ResourceCategory.valueOf(Integer.valueOf(p.get("resourceCategory").toString())));
				}
				throw new CustomException(EnumHandle.GOODSTYPE_NOT_EMPTY.getCode(),
						EnumHandle.GOODSTYPE_NOT_EMPTY.getMessage());
			}).collect(Collectors.toList());
		} else {
			throw new CustomException(EnumHandle.GOODSID_NOT_EMPTY.getCode(),
					EnumHandle.GOODSID_NOT_EMPTY.getMessage());
		}
		return result;
	}

	public static DisplayRequest convertDisplay(Map<String, Object> map) {
		// 验证参数
		ValidateUtil.validateEmptyAndString(map.get("userId"), EnumHandle.PARAMS_INCORRECT);
		ValidateUtil.validateEmptyAndString(map.get("amusementParkId"), EnumHandle.PARAMS_INCORRECT);
		Optional.ofNullable(map.get("goodsId")).orElseThrow(() -> new CustomException(EnumHandle.PARAMS_INCORRECT));
		if (map.get("goodsType") == null || (Integer) map.get("goodsType") != ConstUtil.GOODS_TYPE_COUPON) {
			Optional.ofNullable(map.get("resourceType"))
					.orElseThrow(() -> new CustomException(EnumHandle.PARAMS_INCORRECT));
			Optional.ofNullable(map.get("resourceCategory"))
					.orElseThrow(() -> new CustomException(EnumHandle.PARAMS_INCORRECT));
		}
		return new DisplayRequest((String) map.get("userId"), (String) map.get("amusementParkId"),
				map.get("resourceType") != null ? (Integer) map.get("resourceType") : null,
				map.get("resourceCategory") != null ? ResourceCategory.valueOf((Integer) map.get("resourceCategory"))
						: null,
				(String) map.get("goodsId"), map.get("goodsType") != null ? (Integer) map.get("goodsType") : null,
				true);
	}

	public static OrderStatusRequest converOrderStatus(Map<String, Object> params) {
		ValidateUtil.validateEmptyAndList(params.get("orderIdList"), EnumHandle.PARAMS_INCORRECT);
		Optional.ofNullable(params.get("deviceType"))
				.orElseGet(() -> params.put("deviceType", DeviceTypeEnum.H5PRINT.getCode()));
		Optional.ofNullable(params.get("userId"))
				.orElseThrow(() -> new CustomException(DomainErrorCodeEnum.USERID_PARAM_INCORRECT.getCode(),
						DomainErrorCodeEnum.USERID_PARAM_INCORRECT.getMessage()));
		return new OrderStatusRequest((List<String>) params.get("orderIdList"),
				Integer.parseInt(params.get("deviceType").toString()));
	}
}
