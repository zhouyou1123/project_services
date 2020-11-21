package com.sioeye.youle.run.payment.config;

public enum EnumTradeType {

	H5_FACEBOOK_PAYPAL_DISNEY, H5_LINE_PAYPAL_DISNEY, H5_APPLET_DISNEY, APPLET_DISNEY, HIGHLIGHT_MERGE_APPLET_DISNEY, HIGHLIGHT_APPLET_DISNEY, APPLET_PHOTO, H5_TERMINAL_PHOTO;

	public static boolean validate(String object) {
		if (EnumTradeType.APPLET_DISNEY.toString().equals(object)
				|| EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equals(object)) {
			return true;
		}
		return false;
	}
}
