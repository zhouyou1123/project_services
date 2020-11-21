package com.sioeye.youle.run.order.gateways.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.*;
import com.sioeye.youle.run.order.domain.park.ParkShareActivity;
import com.sioeye.youle.run.order.domain.park.ShareActivity;
import com.sioeye.youle.run.order.domain.price.*;
import com.sioeye.youle.run.order.gateways.dto.DtoFeginResult;
import com.sioeye.youle.run.order.gateways.dto.DtoResult;
import com.sioeye.youle.run.order.gateways.request.ParkGoodsPriceDtoRequest;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.service.feign.IAdminParkFeign;
import com.sioeye.youle.run.order.util.ClientHttpHeader;
import com.sioeye.youle.run.order.util.HttpUtil;
import com.sioeye.youle.run.order.util.Util;
import com.sioeye.youle.run.order.util.ValidateUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Log4j
public class AdminParkServiceClient implements AdminParkService {

	@Value("${disney.park.coupon.url}")
	private String parkCouponUrl;
	@Value("${default.sioeye.photo.promotion.enable}")
	private boolean defaultSioeyePhotoPromotionEnable;
	@Value("${default.sioeye.photo.promotion.summary}")
	private String defaultSioeyePhotoPromotionSummary;
	@Value("${default.sioeye.photo.promotion.description}")
	private String defaultSioeyePhotoPromotionDescription;
	@Autowired
	private IAdminParkFeign iAdminParkFeign;

	private static final Log logger = LogFactory.getLog(AdminParkServiceClient.class);

	@Override
	public CouponGoods getCoupon(String id,String couponName) {

		return onGetCoupon(id,couponName);
	}

	private CouponGoods onGetCoupon(String id,String couponName) {

		// HTTP/1.1 200 OK * { * "success": true, * "value": { * "couponId":
		// "bb0894ff26c949898693f7bf6978c61a", * "parkId":
		// "242f09510d9244d3a4e23ed46fa1f985", * "parkName": "游乐园名称", *
		// "goodsTypes": [0, 2, 3], // 套餐商品范围：0 小视频、1 视频、2 集锦视频、3 照片 *
		// "useDays": 2, // 套餐可用时长（自然日） * "price": 10, * "maxOffers": 100.56, //
		// 用户可见的预估最高可省价格 * "validGamesCount": 6, // 用户可见的预估最高可省价格包含的有效项目数 *
		// "enabled": true, * "publishStart": 315561600000, * "publishEnd":
		// 315698400000, * "createTime": 315561600000, * "updateTime":
		// 315561600000 * } * }

		String message = "{\"couponId\":\"" + id + "\"}";
		JSONObject result = null;
		try {
			String parkCoupon = HttpUtil.httpsRequest(parkCouponUrl, "POST", message,
					ClientHttpHeader.createHeadersMap());
			result = JSONObject.parseObject(parkCoupon);

		} catch (Exception ex) {
			throw new CustomException(DomainErrorCodeEnum.POST_PARK_COUPON_ERROR.getCode(),
					DomainErrorCodeEnum.POST_PARK_COUPON_ERROR.getMessage() + ex.getMessage());
		}

		if (result != null && result.getBoolean("success")) {
			result = result.getJSONObject("value");
			if (result == null) {
				// id 对应的记录不存在
				throw new CustomException(DomainErrorCodeEnum.INVALID_GOODS.getCode(),
						String.format(DomainErrorCodeEnum.INVALID_GOODS.getMessage(), id));
			}

			Park park = new Park(result.getString("parkId"), result.getString("parkName"));
			if (result.getInteger("level") == null || result.getInteger("level") == 0){
				return new CouponGoods(result.getString("couponId"),couponName, park, result.getInteger("useDays"),
						result.getString("timeZone"), new Date(),
						result.getJSONArray("goodsTypes").toJavaList(Integer.class), result.getBoolean("isInPublishPeriod"),
						result.getBoolean("enabled"));
			}else{
				Game game = new Game(result.getString("gameId"), result.getString("gameName"));
				return new GameCouponGoods(result.getString("couponId"),couponName, park, result.getInteger("useDays"),
						result.getString("timeZone"), new Date(),
						result.getJSONArray("goodsTypes").toJavaList(Integer.class), result.getBoolean("isInPublishPeriod"),
						result.getBoolean("enabled"),game);
			}

		} else {
			String errorMessage = result == null ? "" : result.getString("message");
			log.info("\"url\":\"" + parkCouponUrl + "\"," + "\"couponId\":\"" + id + "\"," + "\"error\":\""
					+ errorMessage + "\"");
			throw new CustomException(DomainErrorCodeEnum.POST_PARK_COUPON_ERROR.getCode(),
					DomainErrorCodeEnum.POST_PARK_COUPON_ERROR.getMessage() + errorMessage);
		}

	}

	@Override
	public ParkPrice getPrice(String parkId, String gameId) {
		return getParkPrice(parkId, gameId);
	}

	private ParkPrice getParkPrice(String parkId, String gameId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("parkId", parkId);
		if (StringUtils.isNotEmpty(gameId)) {
			map.put("gameId", gameId);
		}
		try {
			JSONObject result = iAdminParkFeign.getPrice(map);
			// 验证参数
			ValidateUtil.validateRemoteCall("get_price", result, EnumHandle.PRICE_INCORRECT);
			ParkPrice parkPrice = null;
			PromotionPrice proPrice = null;
			CouponPrice couPrice = null;
			List<ShareActivityPrice> shareActivityPriceList = null;
			// 只传parkId即可调用服务
			JSONArray value = result.getJSONArray("value");
			if (value != null && value.size() == 1) {
				JSONObject park = value.getJSONObject(0);
				if (park != null) {
					String objectId = park.getString("objectId");
					Boolean stopped = park.getBoolean("stopped");
					Boolean enabled = park.getBoolean("enabled");
					BigDecimal clipPrice = park.getBigDecimal("price");
					BigDecimal highlightPrice = park.getBigDecimal("highlightPrice");
					BigDecimal photoPrice = park.getBigDecimal("photoPrice");
					// 照片打印价格
					BigDecimal photoPrintPrice = park.getBigDecimal("printPrice");
					String currency = park.getString("currency");
					String timeZone = park.getString("timeZone");
					// 从园区信息获取promotion
					JSONObject promotion = park.getJSONObject("promotion");
					if (promotion != null) {
						Integer fullPriceOrderCount = promotion.getInteger("fullPriceOrderCount");
						Integer promotionOrderLimit = promotion.getInteger("promotionOrderLimit");
						BigDecimal promotionPrice = promotion.getBigDecimal("promotionPrice");
						Date startTime = promotion.getDate("startTime");
						Date endTime = promotion.getDate("endTime");

						Boolean proEnabled = promotion.getBoolean("enabled");
						// 优惠简介和优惠详情
						String summary = promotion.getString("summary");
						String description = promotion.getString("description");
						proPrice = new PromotionPrice(fullPriceOrderCount, promotionOrderLimit, promotionPrice,
								clipPrice, startTime, endTime, proEnabled, summary, description);
					}
					// 从园区信息获取coupon
					JSONObject coupon = park.getJSONObject("coupon");
					// LogUtil.printSuccessLogToJson(logger, "Get coupon of
					// park", coupon);
					if (coupon != null) {
						String couponId = coupon.getString("couponId");
						BigDecimal couponPrice = coupon.getBigDecimal("couponPrice");
						BigDecimal maxOffers = coupon.getBigDecimal("maxOffers");
						Integer validGamesCount = coupon.getInteger("validGamesCount");
						List<Integer> canBuyGoodsType = (List<Integer>) coupon.get("goodsTypes");
						Date publishStart = coupon.getDate("publishStart");
						Date publishEnd = coupon.getDate("publishEnd");
						Integer useDays = coupon.getInteger("useDays");
						Boolean couBnabled = coupon.getBoolean("enabled");
						// 优惠简介和优惠详情
						String summary = coupon.getString("summary");
						String description = coupon.getString("description");
						couPrice = new CouponPrice(couponId, couponPrice, maxOffers, validGamesCount, canBuyGoodsType,
								publishStart, publishEnd, useDays, couBnabled, summary, description);

					}
					// 获取share数据放入到parkPrice
					JSONArray share = park.getJSONArray("share");
					// LogUtil.printSuccessLogToJson(logger, "Get share of
					// park", share);
					if (share != null && share.size() > 0) {
						shareActivityPriceList = new ArrayList<ShareActivityPrice>();
						for (int i = 0; i < share.size(); i++) {
							JSONObject ss = share.getJSONObject(i);
							String thirdShareId = ss.getString("thirdShareId");
							String shareActivityId = ss.getString("shareActivityId");
							String logoUrl = ss.getString("logoUrl");
							// 优惠详情,复用之前分享活动的description字段
							String description = ss.getString("description");
							String shareName = ss.getString("shareName");
							BigDecimal sharePrice = ss.getBigDecimal("sharePrice");
							String platform = ss.getString("platform");
							Boolean isLocalDownload = ss.getBoolean("isLocalDownload");
							Boolean isShowShareLink = ss.getBoolean("isShowShareLink");
							Boolean isUpload = ss.getBoolean("isUpload");
							String uploadApi = ss.getString("uploadApi");
							String checkApi = ss.getString("shareLinkApi");
							Date shareStartTime = ss.getDate("startTime");
							Date shareEndTime = ss.getDate("endTime");
							List<String> uploadGamesBlacklist = (List<String>) ss.get("uploadGamesBlacklist");
							Boolean shareEnabled = ss.getBoolean("enabled");
							Boolean isInPeriod = ss.getBoolean("isInPeriod");
							// 优惠简介
							String summary = ss.getString("summary");
							ShareActivityPrice sap = new ShareActivityPrice(thirdShareId, shareActivityId, logoUrl,
									description, shareName, sharePrice, platform, isLocalDownload, isShowShareLink,
									isUpload, uploadApi, checkApi, shareStartTime, shareEndTime, uploadGamesBlacklist,
									shareEnabled, isInPeriod, summary);
							shareActivityPriceList.add(sap);
						}
					}
					parkPrice = new ParkPrice(objectId, stopped, enabled, clipPrice, highlightPrice, photoPrice,
							photoPrintPrice, currency, timeZone, proPrice, couPrice, shareActivityPriceList);
				}

				return parkPrice;
			}
		} catch (Exception ex) {
			throw new CustomException(DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getCode(),
					DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getMessage() + ex.getMessage());
		}
		return null;
	}

	@Override
	public ParkGoodsPrice getParkGoodsPrice(String parkId, String gameId) {
		try {
			log.info(String.format("parkId:%s,gameId:%s", parkId, gameId));
			ParkGoodsPriceDtoRequest parkPriceDtoRequest = new ParkGoodsPriceDtoRequest(parkId, gameId, null);
			DtoResult<ParkGoodsPrice> dtoResult = iAdminParkFeign.getGoodsPrice(parkPriceDtoRequest);
			log.info("{" +
					"\"parkId\":\"" + parkId+ "\"," +
					"\"gameId\":\"" + (gameId==null?"null":gameId) + "\"," +
					"\"result\":" + JSONObject.toJSONString(dtoResult) +
					"}");
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null) {
				// 这里人工构造一个照片下单的优惠价格,支付价格为0,后期admin-park-price价格改变后，删除这里的逻辑
//				return dtoResult.getValue();
				return mockSaleConfig(dtoResult.getValue());
			} else {
				String code = org.springframework.util.StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getCode();
				String message = org.springframework.util.StringUtils.hasText(dtoResult.getMessage())
						? dtoResult.getMessage() : DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getMessage();
				log.info("{" + "\"url\":\"park/get-goods-price\"," + "\"parkId\":\"" + parkId + "\"," + "\"error\":\""
						+ message + "\"" + "}");
				throw new CustomException(code, message);
			}
		} catch (CustomException bizException) {
			throw bizException;
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"park/get-goods-price\"," + "\"parkId\":\"" + parkId + "\"," + "\"error\":\""
					+ ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getCode(),
					String.format(DomainErrorCodeEnum.POST_PARK_PRICE_ERROR.getMessage(), ex.getMessage()));
		}
	}

	private ParkGoodsPrice mockSaleConfig(ParkGoodsPrice price) {
		// Mock 定点照片
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 3).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(true);
			goodsSaleConfig.setPaymentDescription("电子照片");
			Integer[][] resourceTypes = new Integer[1][8];
			resourceTypes[0][0] = 11;
			resourceTypes[0][1] = 12;
			resourceTypes[0][2] = 13;
			resourceTypes[0][3] = 14;
			resourceTypes[0][4] = 15;
			resourceTypes[0][5] = 16;
			resourceTypes[0][6] = 17;
			resourceTypes[0][7] = 18;
			goodsSaleConfig.setValidResource(resourceTypes);
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setName("电子照片");
		});
		// Mock 机位视频
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 0).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(true);
			goodsSaleConfig.setPaymentDescription("机位视频");
			Integer[][] resourceTypes = new Integer[1][1];
			resourceTypes[0][0] = 1;
			goodsSaleConfig.setValidResource(resourceTypes);
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setName("机位视频");
			//
			Collection<Integer> collection = new ArrayList<Integer>(Arrays.asList(0, 3));
			CouponPrice couponPrice = new CouponPrice("123", BigDecimal.ONE, BigDecimal.ONE, 5, collection, new Date(), new Date(), 1, true,
					"summary", "description");
			p.setCouponPrice(couponPrice);
		});
		// Mock 照片打印
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 5).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(false);
			goodsSaleConfig.setPaymentDescription("照片打印");
			Integer[][] resourceTypes = new Integer[1][8];
			resourceTypes[0][0] = 11;
			resourceTypes[0][1] = 12;
			resourceTypes[0][2] = 13;
			resourceTypes[0][3] = 14;
			resourceTypes[0][4] = 15;
			resourceTypes[0][5] = 16;
			resourceTypes[0][6] = 17;
			resourceTypes[0][7] = 18;
			goodsSaleConfig.setValidResource(resourceTypes);
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setGoodsFlag(1);
			p.getGoodsPrice().setName("照片打印");
		});
		// Mock 打印1+1
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 6).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(false);
			goodsSaleConfig.setPaymentDescription("打印1+1");
			Integer[][] resourceTypes = new Integer[2][1];
			resourceTypes[0][0] = 1;
			resourceTypes[1][0] = 11;
			goodsSaleConfig.setValidResource(resourceTypes);
			Integer[] goodsTypes = new Integer[1];
			goodsTypes[0] = 5;
			goodsSaleConfig.setValidGoods(goodsTypes);
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setGoodsFlag(1);
			p.getGoodsPrice().setName("打印1+1");
		});
		// mock coupon
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 4).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(true);
			goodsSaleConfig.setPaymentDescription("全园套餐");
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setName("全园套餐");
		});
		price.getPriceList().stream().filter(p -> p.getGoodsPrice().getType() == 11).forEach(p -> {
			GoodsSaleConfig goodsSaleConfig = new GoodsSaleConfig();
			goodsSaleConfig.setNeedValidateDuplicate(true);
			goodsSaleConfig.setPaymentDescription("项目套餐");
			p.getGoodsPrice().setSaleConfig(goodsSaleConfig);
			p.getGoodsPrice().setName("项目套餐");
		});
		return price;
	}

	private ParkGoodsPrice mockPresent(ParkGoodsPrice price) {
		price.getPriceList().stream().filter(goods -> goods.getGoodsPrice().getType() == GoodsTypeEnum.PHOTO.getCode())
				.forEach(goods -> {
					Date currentDate = new Date();
					Date startDate = new Date(currentDate.getYear(), currentDate.getMonth(), 1);
					Date endDate = new Date(currentDate.getYear(), currentDate.getMonth(), 29);
					goods.setPresentPrice(new PresentPrice("xxx", true, endDate, true, startDate, "xxx"));
				});
		return price;
	}

	/**
	 * 临时方法，直接构造一个照片的优惠价格,后期直接删除
	 * 
	 * @param dtoResult
	 * @return DtoResult<ParkGoodsPrice>
	 */
	@Deprecated
	private DtoResult<ParkGoodsPrice> createFirstPhotoFreeAmount(DtoResult<ParkGoodsPrice> dtoResult) {
		if (!defaultSioeyePhotoPromotionEnable) {
			return dtoResult;
		}
		ParkGoodsPrice parkGoodsPrice = dtoResult.getValue();
		Optional<GoodsPriceConfiguration> goodsPriceConfiguration = parkGoodsPrice.getPriceList().stream()
				.filter(price -> price.getGoodsPrice().getGoodsType() == GoodsTypeEnum.PHOTO.getCode()).findAny();
		goodsPriceConfiguration.ifPresent(price -> {
			Calendar calendar = Util.getCalendar();
			// 结束时间
			calendar.add(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.add(Calendar.HOUR_OF_DAY, -8);
			Date endDate = calendar.getTime();
			// 开始时间
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			// 照片优惠，三天内不能重复使用
			calendar.add(Calendar.DAY_OF_MONTH, -2);
			calendar.add(Calendar.HOUR_OF_DAY, -8);
			Date startDate = calendar.getTime();
			PromotionPrice proPrice = new PromotionPrice(0, 1, BigDecimal.ZERO, price.getGoodsPrice().getPrice(),
					startDate, endDate, true, defaultSioeyePhotoPromotionSummary,
					defaultSioeyePhotoPromotionDescription);
			goodsPriceConfiguration.get().setPromotionPrice(proPrice);
		});
		return dtoResult;
	}
}
