package com.sioeye.youle.run.order.gateways.repository;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.goods.*;
import com.sioeye.youle.run.order.domain.order.*;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import com.sioeye.youle.run.order.domain.payment.PaymentId;
import com.sioeye.youle.run.order.domain.price.Currency;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.timezone.ParkTimeZone;
import com.sioeye.youle.run.order.gateways.repository.dataobject.*;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryFactory {
	public Order converterOrder(OrderDo orderDo, List<OrderItemDo> orderItemDos,
			List<OrderItemExtendDo> orderItemExtendDos) {

		if (orderDo == null || orderItemDos == null || orderItemExtendDos == null || orderItemDos.size() < 1
				|| orderItemDos.size() != orderItemExtendDos.size()) {
			throw new RuntimeException("from db initialize order is error(order row is not exist).");
		}
		Currency currency = Currency.buildDefault();
		OrderAmount orderAmount = new OrderAmount(orderDo.getOriginalamount(), orderDo.getActualamount(), currency);
		PromotionTypeEnum promotionType = PromotionTypeEnum.valueOf(orderItemDos.get(0).getPromotiontype());
		PayWayEnum payWay = PayWayEnum.valueOf(7);
		Buyer buyer = new Buyer(orderDo.getUsersid(), "");
		ParkTimeZone parkTimeZone = ParkTimeZone.buildDefault();
		PaymentId paymentId = new PaymentId(orderDo.getPaymentid());
		OrderStatusEnum orderStatus = OrderStatusEnum.valueOf(orderDo.getStatus());

		DeviceTypeEnum deviceType = orderDo.getDevicetype() == null ? DeviceTypeEnum.MINIPROGRAM
				: DeviceTypeEnum.valueOf(orderDo.getDevicetype());

		OrderDevice orderDevice = new OrderDevice(orderDo.getDevicerecordid(), deviceType);

		Order order = new Order(orderDo.getObjectid(), orderAmount, promotionType, payWay, buyer,
				orderDo.getOrdertime(), parkTimeZone, paymentId, orderStatus, orderDo.getUpdatetime(),
				Integer.parseInt(orderDo.getType().toString()), Integer.parseInt(orderDo.getOrdertype().toString()),
				orderDevice, orderDo.getSearchid());
		for (int i = 0; i < orderItemDos.size(); i++) {
			Park park = new Park(orderItemDos.get(i).getAmusementparkid(), orderItemDos.get(i).getParkname());
			GoodsTypeEnum goodsType = GoodsTypeEnum.valueOf(orderItemDos.get(i).getGoodstype());
			OrderItemExtendDo orderItemExtendDo = getOrderItemExtendDo(orderItemDos.get(i).getObjectid(),
					orderItemExtendDos);
			if (orderItemExtendDo == null || !StringUtils.hasText(orderItemExtendDo.getExtendcontext())) {
				throw new RuntimeException(
						String.format("the context of  order item extend is not null.(orderid:%s)", order.id()));
			}
			JSONObject map = JSONObject.parseObject(orderItemExtendDo.getExtendcontext());
			Goods goods = null;
			// TODO 重构代码（优化可读性）
			if (GoodsTypeEnum.COUPON.equals(goodsType) || GoodsTypeEnum.GAMECOUPON.equals(goodsType)) {
				// 套票
				List<Integer> canBuyGoodsTypes = null;
				if (map.getJSONArray("canBuyGoodsTypes") != null) {
					canBuyGoodsTypes = map.getJSONArray("canBuyGoodsTypes").toJavaList(Integer.class);
				}
				if (map.getInteger("level") == null || map.getInteger("level") == 0){
					goods = new CouponGoods(orderItemDos.get(i).getGoodsid(), orderItemDos.get(i).getGoodsname(), park,
							map.getInteger("days"), map.getString("timeZone"), orderItemDos.get(i).getCreatetime(),
							map.getDate("startDate"), map.getDate("endDate"), canBuyGoodsTypes);
				}else{
					Game game = new Game(orderItemDos.get(i).getGameid(), orderItemDos.get(i).getGamename());
					goods = new GameCouponGoods(orderItemDos.get(i).getGoodsid(), orderItemDos.get(i).getGoodsname(), park,
							map.getInteger("days"), map.getString("timeZone"), orderItemDos.get(i).getCreatetime(),
							map.getDate("startDate"), map.getDate("endDate"), canBuyGoodsTypes,game);
				}
				OrderItem orderItem = new OrderItem(orderItemDos.get(i).getObjectid(), goods,
						orderItemDos.get(i).getGoodsprice(), orderItemDos.get(i).getCount(),
						orderItemDos.get(i).getActualamount(), null, null, null, null,
						FilingStatusEnum.valueOf(orderItemDos.get(i).getStatus()), orderItemDos.get(i).getCreatetime());
				order.addOrderItem(orderItem);
			}else if (GoodsTypeEnum.PRINT1ADD1.equals(goodsType)) {
				goods = new Print1Add1Goods(orderItemDos.get(i).getGoodsid(), orderItemDos.get(i).getGoodsname(), park);
				OrderItem orderItem = new OrderItem(orderItemDos.get(i).getObjectid(), goods,
						orderItemDos.get(i).getGoodsprice(), orderItemDos.get(i).getCount(),
						orderItemDos.get(i).getActualamount(), null, orderItemDos.get(i).getPreviewurl(),
						orderItemDos.get(i).getDownloadurl(), orderItemDos.get(i).getThumbnailurl(),
						FilingStatusEnum.valueOf(orderItemDos.get(i).getStatus()), orderItemDos.get(i).getCreatetime());
				order.addOrderItem(orderItem);
			} else {
				Game game = new Game(orderItemDos.get(i).getGameid(), orderItemDos.get(i).getGamename());
				Seat seat = null;
				if (orderItemDos.get(i).getSeatid()!=null){
					seat = new Seat(orderItemDos.get(i).getSeatid(), orderItemDos.get(i).getSeatsequenceno(),
							orderItemDos.get(i).getSeatmark());
				}
				Activity activity = null;
				if (orderItemDos.get(i).getActivityid()!=null){
					activity = new Activity(orderItemDos.get(i).getActivityid());
				}
				Clip clip = new Clip(map.getString("downloadUrl"), map.getString("previewUrl"),
						map.getString("thumbnailUrl"), map.getDate("startDate"), map.getInteger("size"),
						map.getInteger("duration"), map.getInteger("width"), map.getInteger("height"));
				OrderItemShareActivty shareActivty = null;
				if (StringUtils.hasText(orderItemDos.get(i).getParkshareid())) {
					JSONObject itemMap = JSONObject.parseObject(
							(orderItemExtendDo != null && StringUtils.hasText(orderItemExtendDo.getItemextendcontext()))
									? orderItemExtendDo.getItemextendcontext() : "{}");
					shareActivty = new OrderItemShareActivty(orderItemDos.get(i).getShareid(),
							orderItemDos.get(i).getParkshareid(), orderItemDos.get(i).getShareuploadflag(),
							itemMap.getString("shareUploadUrl"), itemMap.getString("shareCheckUrl"));
				}
				ResourceCategory resourceCategory = orderItemDos.get(i).getResourceCategory() == null
						? ResourceCategory.Video : ResourceCategory.valueOf(orderItemDos.get(i).getResourceCategory());
				goods = new ResourceGoods(orderItemDos.get(i).getGoodsid(), orderItemDos.get(i).getGoodstype(),
						orderItemDos.get(i).getGoodsname(), park, game, seat, activity, clip,
						orderItemDos.get(i).getResourcetype(), orderItemDos.get(i).getResourcename(), resourceCategory);
				OrderItem orderItem = new OrderItem(orderItemDos.get(i).getObjectid(), goods,
						orderItemDos.get(i).getGoodsprice(), orderItemDos.get(i).getCount(),
						orderItemDos.get(i).getActualamount(), shareActivty, orderItemDos.get(i).getPreviewurl(),
						orderItemDos.get(i).getDownloadurl(), orderItemDos.get(i).getThumbnailurl(),
						FilingStatusEnum.valueOf(orderItemDos.get(i).getStatus()), orderItemDos.get(i).getCreatetime());
				order.addOrderItem(orderItem);
			}
		}

		return order;
	}

	public Order converterOrder(OrderDo orderDo, OrderItemDo orderItemDo, OrderItemExtendDo orderItemExtendDo) {

		return converterOrder(orderDo, Arrays.asList(orderItemDo), Arrays.asList(orderItemExtendDo));
	}

	private OrderItemExtendDo getOrderItemExtendDo(String orderItemId, List<OrderItemExtendDo> orderItemExtendDos) {
		if (orderItemExtendDos == null || !StringUtils.hasText(orderItemId))
			return null;
		Optional<OrderItemExtendDo> any = orderItemExtendDos.stream()
				.filter(extend -> orderItemId.equals(extend.getObjectid())).findAny();
		return any.orElse(null);
	}
}
