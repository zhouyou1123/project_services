package com.sioeye.youle.run.order.domain.goods;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.order.event.PlacedOrderEventMessage;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 套票信息
 */
@Getter
public class CouponGoods extends Goods {
	/**
	 * 套票有效期开始日期,本地时区
	 */
	protected Date startDate;
	/**
	 * 套票有效期结束日期,本地时区
	 */
	protected Date endDate;

	/**
	 * 套票时长
	 */
	protected int days;
	protected String timeZone;

	protected Collection<Integer> canBuyGoodsTypes;

	protected Boolean isInPublishPeriod;

	protected Boolean enabled;
	/**
	 * 0 全园套餐
	 * 1 项目套餐
	 */
	protected Integer level = 0;

	public CouponGoods(String id,String goodsName, Park park, int days, String timeZone, Date createDate,
			Collection<Integer> canBuyGoodsTypes, Boolean isInPublishPeriod, Boolean enabled) {
		this(id,GoodsTypeEnum.COUPON,goodsName,park,days,timeZone,createDate,canBuyGoodsTypes,isInPublishPeriod,enabled);

	}

	protected CouponGoods(String id,GoodsTypeEnum goodsType,String goodsName, Park park, int days, String timeZone, Date createDate,
					   Collection<Integer> canBuyGoodsTypes, Boolean isInPublishPeriod, Boolean enabled) {
		super(id, goodsType.getCode(), goodsName, park);
		setDays(days);
		this.timeZone = timeZone;
		this.isInPublishPeriod = isInPublishPeriod;
		this.enabled = enabled;
		calcStartAndEndDate(createDate, days, timeZone);
		setCanBuyGoodsTypes(canBuyGoodsTypes);
		level = 0;

	}

	public CouponGoods(String id, String goodsName, Park park, int days, String timeZone, Date createDate,
			Date startDate, Date endDate, List<Integer> canBuyGoodsTypes) {
		this(id, GoodsTypeEnum.COUPON,goodsName,park,days,timeZone,createDate,startDate,endDate,canBuyGoodsTypes);
	}

	protected CouponGoods(String id, GoodsTypeEnum goodsType,String goodsName, Park park, int days, String timeZone, Date createDate,
					   Date startDate, Date endDate, List<Integer> canBuyGoodsTypes) {
		super(id, goodsType.getCode(), goodsName, park);
		setDays(days);
		this.timeZone = timeZone;
		this.startDate = startDate;
		this.endDate = endDate;
		this.canBuyGoodsTypes = canBuyGoodsTypes;
		level = 0;
	}


	public boolean returnCouponOverdue() {
		// 指定时区
		Calendar calendar = Calendar.getInstance();
		ZoneId parkTimeZoneId = ZoneId.of(timeZone);
		TimeZone parkTimeZone = TimeZone.getTimeZone(parkTimeZoneId);
		calendar.setTimeZone(parkTimeZone);
		if (endDate.before(calendar.getTime()) || startDate.after(calendar.getTime())) {
			return false;
		}
		return true;
	}

	public void validateCouponOverdue() {
		if (enabled == null || isInPublishPeriod == null || !enabled || !isInPublishPeriod) {
			throw new CustomException(DomainErrorCodeEnum.GOODS_OVERDUE.getCode(),
					DomainErrorCodeEnum.GOODS_OVERDUE.getMessage());
		}
	}

	private void calcStartAndEndDate(Date createDate, int days, String timeZone) {

		Instant instant = createDate.toInstant();
		ZoneId parkTimeZone = ZoneId.of(timeZone);

		LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(instant, parkTimeZone);
		LocalDateTime endLocalDateTime = startLocalDateTime.plusDays(days);
		endLocalDateTime = LocalDateTime.of(endLocalDateTime.getYear(), endLocalDateTime.getMonth(),
				endLocalDateTime.getDayOfMonth(), 0, 0, 0, 0);

		startDate = Date.from(startLocalDateTime.atZone(parkTimeZone).toInstant());
		endDate = Date.from(endLocalDateTime.atZone(parkTimeZone).toInstant());

	}

	private void setDays(int days) {
		if (days < 0) {
			throw new IllegalArgumentException("the days of coupon must greater than 0 ");
		}
		this.days = days;
	}

	private void setCanBuyGoodsTypes(Collection<Integer> canBuyGoodsTypes) {
		if (canBuyGoodsTypes == null || canBuyGoodsTypes.size() < 1) {
			throw new CustomException(DomainErrorCodeEnum.COUPON_BUY_GOODS_TYPE_FAILED.getCode(),
					DomainErrorCodeEnum.COUPON_BUY_GOODS_TYPE_FAILED.getMessage());
		}
		this.canBuyGoodsTypes = canBuyGoodsTypes;
	}

	@Override
	public String getPreviewUrl() {
		return null;
	}

	@Override
	public String getDownloadUrl() {
		return null;
	}

	@Override
	public String getThumbnailUrl() {
		return null;
	}

	@Override
	public void validateOverdue() {
		validateCouponOverdue();
	}

	@Override
	public Activity activity() {
		return null;
	}

	@Override
	public Goods withResource(String previewUrl, String downloadUrl, String thumbnailUrl) {
		return this;
	}

	@Override
	public String toExtends() {


		return "{" + "\"timeZone\":\"" + timeZone + "\"," + "\"startDate\":" + startDate.getTime() + ","
				+ "\"endDate\":" + endDate.getTime() + "," + "\"days\":" + days + "," + "\"level\":" + level + ","
				+ "\"canBuyGoodsTypes\":[" + canBuyGoodsTypesToString() + "]" + "}";
	}
	protected  String canBuyGoodsTypesToString(){

		StringBuilder goodsType = new StringBuilder();
		if (canBuyGoodsTypes != null && canBuyGoodsTypes.size() > 0) {
			for (Integer type : canBuyGoodsTypes) {
				goodsType.append(type + ",");
			}
			goodsType.delete(goodsType.length() - 1, goodsType.length());
		}
		return goodsType.toString();
	}

	@Override
	public String goodsId() {
		return id();
	}

	@Override
	public Integer goodsType() {
		return getType();
	}
}
