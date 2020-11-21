package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;

public interface RunCoreService {

	@Deprecated
	public Goods getClip(String clipId);

	@Deprecated
	public Goods getPhoto(String photoId);

	@Deprecated
	public Goods getPhotoProcessDownUrl(String photoId);

	public String getClipIdByPhotoId(String photoId);

	public Resource getResource(String resourceId, Integer resourceType, ResourceCategory resourceCategory);


}
