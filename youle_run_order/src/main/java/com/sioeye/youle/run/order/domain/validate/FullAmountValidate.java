package com.sioeye.youle.run.order.domain.validate;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FullAmountValidate implements ValidateAmount {

	@Override
	public BigDecimal getActualAmount() {
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getPromotionAmount() {
		return BigDecimal.ZERO;
	}
}
