package com.sioeye.youle.run.order.domain.validate;

import java.math.BigDecimal;
import java.util.function.Consumer;

public interface ValidateAmount {
	// void validateOriginalAmount();
	BigDecimal getActualAmount();

	BigDecimal getPromotionAmount();

	default void validateAmount(ValidateFunction validateOriginalAmount, Consumer<BigDecimal> validateActualAmount) {
		// TODO 验证订单金额现阶段只支持原价，后续可以考虑支持优惠劵和满减 ，而且type 最好和 商品优惠分开
		validateOriginalAmount.validate();
		validateActualAmount.accept(getPromotionAmount());
	}
}
