package com.sioeye.youle.run.order.domain.goods;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import lombok.Getter;

@Getter
public class ResourceGoods extends Goods {

	private Seat seat;
	private Activity activity;
	private Clip clip;
	private Integer resourceType;
	private String resourceName;
	private ResourceCategory resourceCategory;

	public ResourceGoods(String id, Integer goodsType, String goodsName, Park park, Game game, Seat seat,
			Activity activity, Clip clip, Integer resourceType, String resourceName,
			ResourceCategory resourceCategory) {
		super(id, goodsType, goodsName, park);
		this.game = game;
		this.seat = seat;
		this.activity = activity;
		this.clip = clip;
		this.resourceType = resourceType;
		this.resourceName = resourceName;
		this.resourceCategory = resourceCategory;
	}

	public ResourceGoods(Integer goodsType, String goodsName, Resource resource) {
		super(resource.getResourceId(), goodsType, goodsName, resource.getPark().get());
		this.game = resource.getGame().orElse(null);
		this.seat = resource.getSeat().orElse(null);
		this.activity = resource.getActivity().orElse(null);
		this.clip = Clip.builder().downloadUrl(resource.getDownloadUrl()).previewUrl(resource.getPreviewUrl())
				.thumbnailUrl(resource.getThumbnailUrl()).duration(resource.getDuration()==null?null:resource.getDuration().intValue()).size(resource.getSize())
				.width(resource.getWidth()).height(resource.getHeight()).startTime(resource.getCreateTime()).build();
		this.resourceType = resource.getResourceType();
		this.resourceName = resource.getResourceName();
		this.resourceCategory = resource.getResourceCategory();
	}

	@Override
	public String getPreviewUrl() {
		return clip.getPreviewUrl();
	}

	@Override
	public String getDownloadUrl() {
		return clip.getDownloadUrl();
	}

	@Override
	public String getThumbnailUrl() {
		return clip.getThumbnailUrl();
	}

	@Override
	public void validateOverdue() {

	}

	@Override
	public Activity activity() {
		return activity;
	}

	@Override
	public String toExtends() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("previewUrl",this.getClip().getPreviewUrl());
		jsonObject.put("downloadUrl",this.getClip().getDownloadUrl());
		jsonObject.put("thumbnailUrl",this.getClip().getThumbnailUrl());
		jsonObject.put("startDate",this.clip.getStartTime()==null?null:this.clip.getStartTime().getTime());
		jsonObject.put("size",this.getClip().getSize());
		jsonObject.put("duration",this.getClip().getDuration());
		jsonObject.put("width",this.getClip().getWidth());
		jsonObject.put("height",this.getClip().getHeight());
		return jsonObject.toJSONString();
	}

	@Override
	public Goods withResource(String previewUrl, String downloadUrl, String thumbnailUrl) {
		Clip clip = new Clip(downloadUrl, previewUrl, thumbnailUrl, this.getClip().getStartTime(),
				this.getClip().getSize(), this.getClip().getDuration(), this.getClip().getWidth(),
				this.getClip().getHeight());
		return new ResourceGoods(this.id(), this.type, this.name, this.getPark(), this.getGame(), this.getSeat(),
				this.getActivity(), clip, this.resourceType, this.resourceName, this.resourceCategory);
	}

	@Override
	public String goodsId() {
		return id();
	}

	@Override
	public Integer goodsType() {
		return type;
	}

	public Integer getClipWidth() {
		return clip != null ? clip.getWidth() : null;
	}

	public Integer getClipHeight() {
		return clip != null ? clip.getHeight() : null;
	}

	public Integer getClipSize() {
		return clip != null ? clip.getSize() : null;
	}

	public Integer getClipDuration() {
		return clip != null ? clip.getDuration() : null;
	}
}
