package com.sioeye.youle.run.order.domain.price;

import com.esotericsoftware.minlog.Log;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import com.sioeye.youle.run.order.service.impl.OrderBaseService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@Log4j
public class CouponPrice extends ValueObject {
	private String couponId;
	private BigDecimal couponPrice;
	private BigDecimal maxOffers;
	private Integer validGamesCount;
	private Collection<Integer> goodsTypes;
	private Date publishStart;
	private Date publishEnd;
	private Integer useDays;
	private Boolean enabled;
	private String summary;
	private String description;

	public CouponPrice() {
	}

	public boolean isInCouponPeriod() {
		Date dateCurrent = new Date();
		if (dateCurrent.after(publishStart) && dateCurrent.before(publishEnd)) {
			return true;
		} else {
			return false;
		}
	}
}
