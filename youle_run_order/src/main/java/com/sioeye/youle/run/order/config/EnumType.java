package com.sioeye.youle.run.order.config;

public enum EnumType {

	CLIP(0), VIDEO(1), HIGHLIGHT(2),PHOTO(3),COUPON(4),PRINTPHOTO(5),PRINT1ADD1(6),MIRRORCLIP(7),GALLERYCLIP(8);

	private Integer code;

	EnumType(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
