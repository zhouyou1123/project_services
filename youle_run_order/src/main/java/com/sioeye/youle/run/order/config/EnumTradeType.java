package com.sioeye.youle.run.order.config;

import java.util.Optional;

public enum EnumTradeType {

	H5_FACEBOOK_PAYPAL_DISNEY, H5_LINE_PAYPAL_DISNEY, H5_APPLET_DISNEY, APPLET_DISNEY, HIGHLIGHT_MERGE_APPLET_DISNEY, HIGHLIGHT_APPLET_DISNEY, APPLET_PHOTO, COUPON, H5_TERMINAL_PHOTO, PRINT1ADD1;

	public static boolean validate(String object) {
		if (EnumTradeType.APPLET_DISNEY.toString().equals(object)
				|| EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equals(object)) {
			return true;
		}
		return false;
	}

	public static void validateTradeType(Object object) {
		boolean result = false;
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(EnumHandle.TRADE_TYPE_ERROR));
		for (EnumTradeType enumTradeType : EnumTradeType.values()) {
			if (enumTradeType.toString().equals(object)) {
				result = true;
				break;
			}
		}
		if (!result) {
			throw new CustomException(EnumHandle.TRADE_TYPE_ERROR);
		}
	}
}