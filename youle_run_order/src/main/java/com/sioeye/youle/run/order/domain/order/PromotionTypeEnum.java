package com.sioeye.youle.run.order.domain.order;

public enum PromotionTypeEnum {

	/**
	 * 原价
	 */
	FULL(0),
	/**
	 * 游乐园折扣
	 */
	PARKDISCOUNT(1),
	/**
	 * 套票
	 */
	COUPON(2),
	/**
	 * 分享活动
	 */
	SHARE(3),
	/**
	 * 赠送
	 */
	PRESENT(4);

	private int code;

	public int getCode() {
		return code;
	}

	PromotionTypeEnum(int code) {
		this.code = code;
	}

	public static PromotionTypeEnum valueOf(Integer code) {
		if (code == null) {
			return PromotionTypeEnum.FULL;
		}
		for (PromotionTypeEnum discountType : values()) {
			if (discountType.getCode() == code) {
				return discountType;
			}
		}
		throw new RuntimeException(String.format("promotionType not support code:%s", code));
	}
}
