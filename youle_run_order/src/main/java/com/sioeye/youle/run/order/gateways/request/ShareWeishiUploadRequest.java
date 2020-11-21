package com.sioeye.youle.run.order.gateways.request;

import com.sioeye.youle.run.order.domain.goods.Clip;
import com.sioeye.youle.run.order.domain.goods.ClipGoods;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShareWeishiUploadRequest {

	private String seq;
	private String video_url;
	private String cover_url;
	private String title;
	private Integer width;
	private Integer height;
	private Integer duration;
	private String gameId;
	private String shareLinkApi;

	public ShareWeishiUploadRequest(String shareCheckApi, String orderId, ClipGoods clipGoods) {
		Clip clip = clipGoods.getClip();
		this.seq = orderId;
		this.video_url = clip.getDownloadUrl();
		this.cover_url = clip.getThumbnailUrl();
		this.title = clipGoods.getPark().getParkName() + "-" + clipGoods.getGame().getGameName();
		this.width = clip.getWidth();
		this.height = clip.getHeight();
		this.duration = clip.getDuration();
		this.gameId = clipGoods.getGame().id();
		this.shareLinkApi = shareCheckApi;
	}
}
