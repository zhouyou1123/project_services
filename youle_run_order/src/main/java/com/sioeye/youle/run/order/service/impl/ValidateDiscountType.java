/**  
* <p>Title: ValidateDiscountType.java</p>  
* <p>Description: </p>    
* @author GongGuowei  
* @date 2019年6月12日  
* @version 1.0  
*/
package com.sioeye.youle.run.order.service.impl;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.price.ParkPrice;
import com.sioeye.youle.run.order.domain.price.ShareActivityPrice;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.service.intf.IValidateDiscountType;
import com.sioeye.youle.run.order.util.ConstUtil;
import com.sioeye.youle.run.order.util.LogUtil;
import com.sioeye.youle.run.order.util.ValidateUtil;

/**
 * @Author GuoGongwei
 * @Time: 2019年6月12日
 * @Description:
 */
@RefreshScope
@Service
@Deprecated
public class ValidateDiscountType implements IValidateDiscountType {

	@Autowired
	private BoughtService buyerCouponService;
	@Autowired
	private AdminParkService parkService;

	private static final Log logger = LogFactory.getLog(ValidateDiscountType.class);

	@Override
	public BuyerCoupon validateCoupon(Map<String, Object> params, Date orderTime, Integer goodsType) {
		String logKey = null;
		String logValue = null;
		// 获取用户id信息
		String buyerId = params.get("usersId").toString();
		LogUtil.printSuccessLogToJson(logger, "usersId", "获取前端usersId=" + buyerId);
		// 获取游乐园id
		String parkId = params.get("amusementParkId").toString();
		LogUtil.printSuccessLogToJson(logger, "amusementParkId", "获取前端amusementParkId=" + parkId);
		// 前端传来的原价
		String originalAmount = params.get("originalAmount").toString();
		BigDecimal webOriginalAmount = new BigDecimal(originalAmount);
		// 前端传来的实际支付价格
		String actualAmount = params.get("actualAmount").toString();
		BigDecimal webActualAmount = new BigDecimal(actualAmount);
		LogUtil.printSuccessLogToJson(logger, "actualAmount", "获取前端actualAmount=" + webActualAmount);
		// 用游乐园id查询该游乐园设置的小视频原价和照片原价
		this.validateGameId(params.get("gameId"), EnumHandle.PLACE_ORDER_GAMEID_PARAM_INCORRECT);
		ParkPrice parkPrice = parkService.getPrice(params.get("amusementParkId").toString(),
				params.get("gameId").toString());
		BigDecimal clipOriginalPrice = parkPrice.getClipPrice();
		BigDecimal photoOriginalPrice = parkPrice.getPhotoPrice();
		BigDecimal highlightOriginaPrice = parkPrice.getHighlightPrice();
		logKey = "validate original amount";
		logValue = "The original price of the front-end transmission is not equal to the original price of the sioeye youle Management platform setting!";
		// 校验以套餐下单小视频、照片或者集锦视频时，前端传输来的原价和游乐园后台设置的原价是否相等，不相等则不能下单
		String tradeType = params.get("tradeType") == null ? EnumTradeType.APPLET_DISNEY.toString()
				: params.get("tradeType").toString();
		if (EnumTradeType.APPLET_DISNEY.toString().equals(tradeType)) {
			this.validatePrice(webOriginalAmount, clipOriginalPrice, logKey, logValue,
					EnumHandle.VALIDATE_ORIGINAL_AMOUNT);
		} else if (EnumTradeType.APPLET_PHOTO.toString().equals(tradeType)) {
			this.validatePrice(webOriginalAmount, photoOriginalPrice, logKey, logValue,
					EnumHandle.VALIDATE_ORIGINAL_AMOUNT);
		} else if (EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equals(tradeType)) {
			this.validatePrice(webOriginalAmount, highlightOriginaPrice, logKey, logValue,
					EnumHandle.VALIDATE_ORIGINAL_AMOUNT);
		}
		// try {
		BuyerCoupon buyerCoupon = buyerCouponService.getBuyerCoupon(buyerId, parkId);

		if (buyerCoupon == null) {
			LogUtil.printFailedLogToJson(logger, "验证获取购买套票", "验证获取购买套票未通过");
			throw new CustomException(DomainErrorCodeEnum.BUYER_NOTBUY_COUPON.getCode(),
					DomainErrorCodeEnum.BUYER_NOTBUY_COUPON.getMessage());
		}
		// 验证套票是否过期:返回true为过期，返回false为没过期
		if (buyerCoupon.validatePeriodOverdue(orderTime)) {
			throw new CustomException(DomainErrorCodeEnum.COUPON_NOTIN_PERIOD.getCode(),
					DomainErrorCodeEnum.COUPON_NOTIN_PERIOD.getMessage());
		}

		// 验证价格是否相等
		logKey = "validate coupon price";
		logValue = "The coupon price and the parkPrice's price is not equal";
		this.validatePrice(webActualAmount, ConstUtil.USE_COUPON_BUY_CLIP_PRICE, logKey, logValue,
				EnumHandle.COUPON_PRICE_VALIDATE_ERROR);
		// 验证商品类型
		Collection<Integer> canBuyGoodsType = buyerCoupon.getCanBuyGoodsTypes();
		LogUtil.printSuccessLogToJson(logger, "获取商品类型", "canBuyGoodsType=" + canBuyGoodsType);
		this.validateGoodsType(canBuyGoodsType, goodsType);
		return buyerCoupon;
		// } catch (Exception e) {
		// LogUtil.printFailedLogToJson(logger, "Validate coupon is failed!",
		// "Validate coupon is failed!and the stack is " + e);
		// throw new CustomException(EnumHandle.VALIDATE_COUPON_FAILED);
		// }
	}

	@Override
	public JSONObject validateShare(Map<String, Object> params, String gameId) {
		JSONObject shareJson = new JSONObject();
		String parkId = params.get("amusementParkId").toString();
		ValidateUtil.validateEmptyAndUuid(params.get("parkShareActivityId"), EnumHandle.VALIDATE_SHARE_ACTIVITY_FAILED);
		String parkShareActivityId = params.get("parkShareActivityId").toString();
		// 前端传来的原价
		String originalAmount = params.get("originalAmount").toString();
		BigDecimal webOriginalAmount = new BigDecimal(originalAmount);
		// 获取实际支付价格
		String amount = params.get("actualAmount").toString();
		BigDecimal actualAmount = new BigDecimal(amount);
		// 用游乐园id查询出该游乐园的设置
		ParkPrice price = parkService.getPrice(parkId, gameId);
		// 获取该游乐园设置的小视频原价
		BigDecimal clipOriginalPrice = price.getClipPrice();
		String logKey = "validate original amount";
		String logValue = "The original price of the front-end transmission is not equal to the original price of the sioeye youle Management platform setting!";
		// 校验以分享活动下单小视频时，前端传输来的原价和游乐园后台设置的原价是否相等，不相等则不能下单
		this.validatePrice(webOriginalAmount, clipOriginalPrice, logKey, logValue, EnumHandle.VALIDATE_ORIGINAL_AMOUNT);

		List<ShareActivityPrice> shareList = price.getShareActivityPriceList();
		if (shareList != null && shareList.size() > 0) {
			for (ShareActivityPrice share : shareList) {
				if (share.getShareActivityId().equals(parkShareActivityId)) {
					// TODO: 疑似缺少分享活动状态的判断
					if (!share.getEnabled()) {
						LogUtil.printFailedLogToJson(logger, "Sharing activity is not enabled",
								"Sharing activity is not enabled");
						throw new CustomException(EnumHandle.SHARE_ACTIVITY_IS_NOT_ENABLE);
					} else if (!share.getIsInPeriod()) {
						LogUtil.printFailedLogToJson(logger, "Sharing activity is not valid",
								"Sharing activity is not valid");
						throw new CustomException(EnumHandle.SHARE_ACTIVITY_IS_NOT_VALID);
					} else if (share.getSharePrice() == null) {
						LogUtil.printFailedLogToJson(logger, "share price not set",
								"Share acrivity price isn't set in sioeye youle management platform!");
						throw new CustomException(EnumHandle.SHARE_PRICE_NOT_SET);
					} else if (share.getSharePrice().compareTo(actualAmount) == 0) {
						String thirdShareId = share.getThirdShareId();
						String shareName = share.getShareName();
						Boolean finalIsUpload = share.getIsUpload();
						Boolean isUpload = false;
						if (finalIsUpload) {
							isUpload = share.checkGameCanUploadShare(gameId);
						}
						String parkShareId = params.get("parkShareActivityId").toString();
						// 返回分享活动的字段，准备更新order表
						shareJson.put("thirdShareId", thirdShareId);
						shareJson.put("shareName", shareName);
						shareJson.put("isUpload", isUpload);
						shareJson.put("parkShareId", parkShareId);
						return shareJson;
					} else {
						LogUtil.printFailedLogToJson(logger, "Validate share activity price",
								"Validate price of share activity is failed!");
						throw new CustomException(EnumHandle.SHARE_ACTIVITY_VALIDATE_FAILED);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @Description:1、验证游乐园后台设置的原价与接口传来的原价是否相等 2、验证以套餐下单小视频或照片或集锦视频时，接口传来的实际支付价格是否为0元
	 * @Author GuoGongwei
	 * @Time: 2019年6月13日下午2:10:42
	 * @param webPrice
	 * @param remotePrice
	 */
	private void validatePrice(BigDecimal webPrice, BigDecimal remotePrice, String logKey, String logValue,
			EnumHandle enumHandle) {
		if (webPrice.compareTo(remotePrice) != 0) {
			LogUtil.printFailedLogToJson(logger, logKey, logValue);
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 
	 * @Description:验证商品类型
	 * @Author GuoGongwei
	 * @Time: 2019年6月13日下午2:29:21
	 * @param canBuyGoodsType
	 * @param goodsType
	 */
	private void validateGoodsType(Collection<Integer> canBuyGoodsType, Integer goodsType) {
		if (!canBuyGoodsType.contains(goodsType)) {
			LogUtil.printFailedLogToJson(logger, "validate goods type",
					"ParkPrice's canBuyGoodsType does not contain the goodsType from the front page.");
			throw new CustomException(EnumHandle.CANBUYGOODSTYPE_VALIDATE_ERROR);
		}
	}

	/**
	 * 
	 * @Description:校验gameId是否为空
	 * @Author GuoGongwei
	 * @Time: 2019年7月17日上午11:19:54
	 * @param gameId
	 * @param enumHandle
	 */
	private void validateGameId(Object gameId, EnumHandle enumHandle) {
		Optional.ofNullable(gameId).orElseThrow(() -> new CustomException(enumHandle));
	}

}
