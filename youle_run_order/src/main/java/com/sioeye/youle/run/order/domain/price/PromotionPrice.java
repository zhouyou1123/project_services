package com.sioeye.youle.run.order.domain.price;

import com.sioeye.youle.run.order.domain.common.ValueObject;
import com.sioeye.youle.run.order.domain.timezone.ParkTimeZone;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class PromotionPrice extends ValueObject {
	private Integer fullPriceOrderCount;
	private Integer promotionOrderLimit;
	private BigDecimal promotionPrice;
	private BigDecimal originalPrice;
	private Date startTime;
	private Date endTime;
	private Boolean enabled;
	private String summary;
	private String description;

	public BigDecimal calcDiscountPrice(int fullPriceOrderCount, int disCountPriceOrderCount) {
		Date dateCurrent = new Date();
		if (dateCurrent.before(startTime) || dateCurrent.after(endTime)) {
			return originalPrice;
		}

		// TODO 实际计算
		if (fullPriceOrderCount >= this.fullPriceOrderCount
				&& (this.promotionOrderLimit == 0 || disCountPriceOrderCount < this.promotionOrderLimit)) {
			return promotionPrice;
		} else {
			return originalPrice;
		}
	}

	public PromotionPrice() {
	}

	public PromotionPrice build(BigDecimal originalPrice) {
		return new PromotionPrice(this.fullPriceOrderCount, this.promotionOrderLimit, this.promotionPrice,
				originalPrice, this.startTime, this.endTime, this.enabled, this.summary, this.description);
	}

	public Date calcLocalPeriodStartDate(String timeZone, int periodType) {
		// return new ParkTimeZone(timeZone).getPeriodEndDate(new Date(),0);
		return new ParkTimeZone(timeZone).getPeriodStartDate(new Date(), periodType);
	}

	public Date calcLocalPeriodEndDate(String timeZone, int periodType) {
		return new ParkTimeZone(timeZone).getPeriodEndDate(new Date(), periodType);
		// return new ParkTimeZone(timeZone).getPeriodStartDate(new Date(),0);
	}

	/**
	 * 
	 * @Description:判断客户端本地时间是否在游乐园优惠有效期内
	 * @Author GuoGongwei
	 * @Time: 2019年7月18日下午2:14:19
	 * @param timeZone
	 * @return
	 */
	public Boolean validateInPromotionPeriod(String timeZone) {
		ZoneId localTimeZone = ZoneId.of(timeZone);
		Date nowUtc = new Date();
		LocalDateTime localNow = LocalDateTime.ofInstant(nowUtc.toInstant(), localTimeZone);
		LocalDateTime localStartTime = LocalDateTime.ofInstant(this.startTime.toInstant(), localTimeZone);
		LocalDateTime localEndTime = LocalDateTime.ofInstant(this.endTime.toInstant(), localTimeZone);
		if (localStartTime.isBefore(localNow) && localEndTime.isAfter(localNow)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Description:判断是否使用优惠活动
	 * @Author GuoGongwei
	 * @Time: 2019年7月18日下午2:40:39
	 * @param fullPriceOrderCount
	 * @param disCountPriceOrderCount
	 * @param timeZone
	 * @return
	 */
	public Boolean validateUsePromotion(int fullPriceOrderCount, int disCountPriceOrderCount, String timeZone) {
		if ((fullPriceOrderCount >= this.fullPriceOrderCount
				&& (this.promotionOrderLimit == 0 || disCountPriceOrderCount < this.promotionOrderLimit))
				&& this.enabled && this.validateInPromotionPeriod(timeZone)) {
			return true;
		}
		return false;
	}
}
