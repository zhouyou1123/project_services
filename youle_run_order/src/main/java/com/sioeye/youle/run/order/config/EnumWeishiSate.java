package com.sioeye.youle.run.order.config;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年4月20日
 *
 * @fileName EnumWeishiSate.java
 *
 * @todo 上传微视视频状态
 */
public enum EnumWeishiSate {

	PUBLISHING(1), DOWNLOADING_COVER(2), UPLOADING_COVER(3), DOWNLOADING_VIDEO(4), UPLOADING_VIDEO(
			5), VALIDATE_PARAM_FAILURE(6), DOWNLOAD_COVER_FAILURE(7), UPLOAD_COVER_FAILURE(8), DOWNLOAD_VIDEO_FAILURE(
					9), UPLOAD_VIDEO_FAILURE(10), PUBLISH_VIDEO_FAILURE(11), PUBLISH_SUCCESS(12);

	private Integer code;

	EnumWeishiSate(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}