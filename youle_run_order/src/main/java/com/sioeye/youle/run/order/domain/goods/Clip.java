package com.sioeye.youle.run.order.domain.goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
@Builder
public class Clip {
	private String downloadUrl;
	private String previewUrl;
	private String thumbnailUrl;
	private Date startTime;
	private Integer size;
	private Integer duration;
	private Integer width;
	private Integer height;
	private String qrCode;

	public Clip(String downloadUrl, String previewUrl, String thumbnailUrl, Date startTime, Integer size,
			Integer duration, Integer width, Integer height) {
		this.downloadUrl = downloadUrl;
		this.previewUrl = previewUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.startTime = startTime;
		this.size = size;
		this.duration = duration;
		this.width = width;
		this.height = height;
	}
}
