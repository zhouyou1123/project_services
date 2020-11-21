package com.sioeye.youle.run.order.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sioeye.youle.run.order.domain.goods.ClipGoods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.order.OrderItemShareActivty;
import com.sioeye.youle.run.order.interfaces.VideoShareService;

@Component
public class PromotionActivityService {

	@Autowired
	private VideoShareService videoShareService;

	/**
	 * 分享处理
	 * 
	 * @param order
	 *            void
	 */
	public void share(Order order) {
		Optional<OrderItem> oOrderItem = order.stream()
				.filter(orderItem -> orderItem.getGoods().goodsType() == GoodsTypeEnum.CLIP.getCode()
						&& orderItem.getShareActivty() != null && orderItem.getShareActivty().getUploadFlag())
				.findAny();
		if (oOrderItem.isPresent()) {
			this.shareWeishi(order.id(), oOrderItem.get());
		}
	}

	/**
	 * 分享微视
	 * 
	 * @param orderItemShareActivty
	 * @return boolean
	 */
	private boolean shareWeishi(String orderId, OrderItem orderItem) {
		OrderItemShareActivty orderItemShareActivty = orderItem.getShareActivty();
		// 获取视频信息
		ClipGoods clipGoods = (ClipGoods) orderItem.getGoods();
		return videoShareService.uploadWeishi(orderItemShareActivty.getShareUploadUrl(),
				orderItemShareActivty.getShareCheckUrl(), orderId, clipGoods);
	}
}
