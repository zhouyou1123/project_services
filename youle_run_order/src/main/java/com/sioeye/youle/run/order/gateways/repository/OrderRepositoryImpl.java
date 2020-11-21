package com.sioeye.youle.run.order.gateways.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sioeye.youle.run.order.domain.goods.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.order.CouponOrder;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderIdUtils;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.order.PromotionTypeEnum;
import com.sioeye.youle.run.order.gateways.repository.dao.CouponOrderDao;
import com.sioeye.youle.run.order.gateways.repository.dao.OrderDao;
import com.sioeye.youle.run.order.gateways.repository.dao.OrderItemDao;
import com.sioeye.youle.run.order.gateways.repository.dao.OrderItemExtendDao;
import com.sioeye.youle.run.order.gateways.repository.dao.UserCouponDao;
import com.sioeye.youle.run.order.gateways.repository.dataobject.CouponOrderDo;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderDo;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemDo;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemExtendDo;
import com.sioeye.youle.run.order.gateways.repository.dataobject.UserCouponDo;
import com.sioeye.youle.run.order.util.ExceptionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderRepositoryImpl implements OrderRepository {

	private OrderRepositoryFactory repositoryFactory;
	private OrderDao orderDao;
	private OrderItemDao orderItemDao;
	private OrderItemExtendDao orderItemExtendDao;
	private UserCouponDao userCouponDao;
	private CouponOrderDao couponOrderDao;

	public OrderRepositoryImpl(OrderDao orderDao, OrderItemDao orderItemDao, OrderItemExtendDao orderItemExtendDao,
			UserCouponDao userCouponDao, CouponOrderDao couponOrderDao, OrderRepositoryFactory repositoryFactory) {
		this.orderDao = orderDao;
		this.orderItemDao = orderItemDao;
		this.orderItemExtendDao = orderItemExtendDao;
		this.userCouponDao = userCouponDao;
		this.repositoryFactory = repositoryFactory;
		this.couponOrderDao = couponOrderDao;
	}

	@Override
	public int getOrderCountByPaid(String buyer, String goodsId) {
		return orderItemDao.getOrderCountByUserPaid(buyer, goodsId);
	}

	@Override
	public Order getLastPaidOrderByGoodsId(String buyer, String goodsId, Integer goodsType) {
		OrderItemDo orderItemDo = orderItemDao.getLastPaidOrderByGoodsId(buyer, goodsId, goodsType);
		if (orderItemDo == null) {
			return null;
		}
		OrderItemExtendDo orderItemExtendDo = orderItemExtendDao.selectByPrimaryKey(orderItemDo.getObjectid());
		OrderDo orderDo = orderDao.selectByPrimaryKey(orderItemDo.getOrderid());
		return repositoryFactory.converterOrder(orderDo, orderItemDo, orderItemExtendDo);
	}

	@Override
	public Order getLastOrderByGoodsId(String buyer, String goodsId, Integer goodsType) {

		OrderItemDo lastOrderIdByUserNotPay = orderItemDao.getLastOrderByGoodsId(buyer, goodsId, goodsType);
		if (lastOrderIdByUserNotPay == null) {
			return null;
		}
		OrderDo orderDo = orderDao.selectByPrimaryKey(lastOrderIdByUserNotPay.getOrderid());

		OrderItemExtendDo orderItemExtendDo = orderItemExtendDao
				.selectByPrimaryKey(lastOrderIdByUserNotPay.getObjectid());

		return repositoryFactory.converterOrder(orderDo, lastOrderIdByUserNotPay, orderItemExtendDo);
	}

	@Override
	public Order getLastPaidOrderByParkId(String buyer, String parkId, Integer goodsType) {

		OrderItemDo lastOrderByPaid = orderItemDao.getLastPaidOrderByParkId(buyer, parkId, goodsType);
		if (lastOrderByPaid == null) {
			return null;
		}
		OrderDo orderDo = orderDao.selectByPrimaryKey(lastOrderByPaid.getOrderid());

		OrderItemExtendDo orderItemExtendDo = orderItemExtendDao.selectByPrimaryKey(lastOrderByPaid.getObjectid());

		return repositoryFactory.converterOrder(orderDo, lastOrderByPaid, orderItemExtendDo);

	}

	@Override
	public Order getLastOrderByParkId(String buyer, String parkId, Integer goodsType) {

		OrderItemDo lastOrderByUser = orderItemDao.getLastOrderByParkId(buyer, parkId, goodsType);
		if (lastOrderByUser == null) {
			return null;
		}
		OrderDo orderDo = orderDao.selectByPrimaryKey(lastOrderByUser.getOrderid());

		OrderItemExtendDo orderItemExtendDo = orderItemExtendDao.selectByPrimaryKey(lastOrderByUser.getObjectid());

		return repositoryFactory.converterOrder(orderDo, lastOrderByUser, orderItemExtendDo);

	}

	@Override
	public int getOrderCountByFullPrice(String buyer, String parkId, Integer goodsType, Date startDate,
			Date endDate) {
		return orderItemDao.getOrderFullPriceCount(buyer, parkId, goodsType, startDate, endDate);
	}

	@Override
	public int getOrderCountByFreePrice(String buyer, String parkId, Integer goodsType, Date startDate,
			Date endDate) {
		return orderItemDao.getOrderPromotionPriceCount(buyer, parkId, goodsType, startDate, endDate);
	}

	@Override
	public int getOrderCountByPresent(String buyer, String parkId, Integer goodsType, Date startDate,
			Date endDate) {
		return orderItemDao.getOrderPresentCount(buyer, parkId, goodsType, startDate, endDate);
	}

	@Override
	public BuyerCoupon getBuyerCoupon(String buyerId, String parkId) {
		UserCouponDo userCoupon = userCouponDao.getUserCoupon(buyerId, parkId);
		return convertToBuyerCoupon(userCoupon);
	}

	@Override
	public BuyerCoupon getBuyerGameCoupon(String buyerId, String goodsId) {
		UserCouponDo userCoupon = userCouponDao.getUserGameCoupon(buyerId, goodsId);
		return convertToBuyerCoupon(userCoupon);
	}
	@Override
	public BuyerCoupon getBuyerGameCouponByGameId(String buyerId, String parkId,String gameId){
		UserCouponDo userCoupon = userCouponDao.getUserGameCouponByGameId(buyerId, parkId,gameId);
		return convertToBuyerCoupon(userCoupon);
	}

	private BuyerCoupon convertToBuyerCoupon(UserCouponDo userCoupon){
		if (userCoupon == null)
			return null;
		Buyer buyer = new Buyer(userCoupon.getUserid(), userCoupon.getOpenid());
		Park park = new Park(userCoupon.getAmusementparkid(), userCoupon.getParkname());
		List<Integer> canBuyGoodsTypes = null;
		if (userCoupon.getCanbuygoodstypes() != null) {
			canBuyGoodsTypes = Arrays.asList(userCoupon.getCanbuygoodstypes());
		}
		BuyerCoupon buyerCoupon = new BuyerCoupon(buyer, userCoupon.getStartdate(), userCoupon.getEnddate(),
				userCoupon.getTimezone(), userCoupon.getOrderid(), park, canBuyGoodsTypes,userCoupon.getLevel(),userCoupon.getGameid(),userCoupon.getCouponid());
		return buyerCoupon;
	}

	@Transactional
	@Override
	public void saveBuyerCoupon(BuyerCoupon buyerCoupon) {
		UserCouponDo userCoupon = null;
		if (buyerCoupon.getLevel() == 0){
			userCoupon = userCouponDao.getUserCoupon(buyerCoupon.getBuyer().id(), buyerCoupon.getPark().id());

		}else{
			userCoupon = userCouponDao.getUserGameCoupon(buyerCoupon.getBuyer().id(), buyerCoupon.getCouponId());
		}
		if (userCoupon != null) {
			// 修改
			convertToUserCouponDo(buyerCoupon,userCoupon);
			userCouponDao.updateByPrimaryKeySelective(userCoupon);
		} else {
			// 新增
			userCoupon = new UserCouponDo();
			userCoupon.setObjectid(OrderIdUtils.uuid());
			convertToUserCouponDo(buyerCoupon,userCoupon);
			userCouponDao.insertSelective(userCoupon);
		}
	}
	private void convertToUserCouponDo(BuyerCoupon sourceBuyerCoupon, UserCouponDo  targetUserCouponDo){
		targetUserCouponDo.setAmusementparkid(sourceBuyerCoupon.getPark().id());
		targetUserCouponDo.setEnddate(sourceBuyerCoupon.getCouponEndDate());
		targetUserCouponDo.setOpenid(sourceBuyerCoupon.getBuyer().getOpenId());
		targetUserCouponDo.setOrderid(sourceBuyerCoupon.getOrderId());
		targetUserCouponDo.setParkname(sourceBuyerCoupon.getPark().getParkName());
		targetUserCouponDo.setStartdate(sourceBuyerCoupon.getCouponStartDate());
		targetUserCouponDo.setTimezone(sourceBuyerCoupon.getTimeZone());
		targetUserCouponDo.setUpdatetime(new Date());
		targetUserCouponDo.setUserid(sourceBuyerCoupon.getBuyer().id());
		targetUserCouponDo.setCanbuygoodstypes(sourceBuyerCoupon.getCanBuyGoodsTypes().toArray(new Integer[0]));
		targetUserCouponDo.setLevel(sourceBuyerCoupon.getLevel());
		targetUserCouponDo.setGameid(sourceBuyerCoupon.getGameId());
		targetUserCouponDo.setCouponid(sourceBuyerCoupon.getCouponId());
	}

	@Transactional
	@Override
	public void saveOrder(Order order) {
		try {
			// 保存order基本信息
			onSaveOrder(order);
			// 保存order item
			onSaveOrderItem(order);
			// 如果使用套餐购买，那么需要保存couponorder表
			onSaveCouponOrder(order);
		} catch (DuplicateKeyException ex) {
			ExceptionUtil.log(log::error, ex);
			throw new CustomException(DomainErrorCodeEnum.DUPLICATE_BUY.getCode(),
					String.format(DomainErrorCodeEnum.DUPLICATE_BUY.getMessage(), order.id()));
		} catch (Exception ex) {
			ExceptionUtil.log(log::error, ex);
			throw CustomException.build(DomainErrorCodeEnum.SAVE_ORDER_FAILED);
		}
	}

	@Override
	public Order getOrder(String orderId) {

		OrderDo orderDo = orderDao.selectByPrimaryKey(orderId);
		List<OrderItemDo> orderItemDos = orderItemDao.selectOrderItems(orderId);
		List<OrderItemExtendDo> orderItemExtendDos = orderItemExtendDao.selectOrderItemExtends(orderId);

		return repositoryFactory.converterOrder(orderDo, orderItemDos, orderItemExtendDos);

	}

	@Override
	public Order getOrderByUser(String orderId, String buyer) {
		OrderDo orderDo = orderDao.selectByPrimaryKey(orderId);
		if (orderDo == null || (orderDo.getType().intValue() != EnumType.PRINTPHOTO.getCode()
				&& orderDo.getType().intValue() != EnumType.PRINT1ADD1.getCode()
				&& !buyer.equals(orderDo.getUsersid()))) {
			throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
		}
		// OrderDo orderDo = orderDao.getOrderByUser(orderId, buyer);
		List<OrderItemDo> orderItemDos = orderItemDao.selectOrderItems(orderId);
		List<OrderItemExtendDo> orderItemExtendDos = orderItemExtendDao.selectOrderItemExtends(orderId);
		return repositoryFactory.converterOrder(orderDo, orderItemDos, orderItemExtendDos);
	}

	@Transactional
	@Override
	public void savePaidOrder(Order order) {
		// 保存订单信息
		OrderDo orderDo = new OrderDo();
		orderDo.setObjectid(order.id());
		// orderDo.setPaymenttime(order.getPaymentDate());
		orderDo.setUpdatetime(new Date());
		orderDo.setStatus((short) order.getOrderStatus().getCode());
		orderDao.updateByPrimaryKeySelective(orderDo);

		Iterator<OrderItem> iterator = order.iterator();
		while (iterator.hasNext()) {
			// 保存订单项
			OrderItem orderItem = iterator.next();
			OrderItemDo orderItemDo = new OrderItemDo();
			orderItemDo.setObjectid(orderItem.id());
			orderItemDo.setUpdatetime(new Date());
			orderItemDo.setPreviewurl(orderItem.getPreviewUrl());
			orderItemDo.setDownloadurl(orderItem.getDownloadUrl());
			orderItemDo.setThumbnailurl(orderItem.getThumbnailUrl());
			orderItemDo.setStatus(orderItem.getFilingStatus().getCode());
			if (orderItem.getShareActivty() != null) {
				orderItemDo.setShareid(orderItem.getShareActivty().getShareId());
				orderItemDo.setParkshareid(orderItem.getShareActivty().getParkShareId());
				orderItemDo.setShareuploadflag(orderItem.getShareActivty().getUploadFlag());
				orderItemDo.setSharecheckurl(orderItem.getShareActivty().getShareCheckUrl());
			}
			orderItemDao.updateByPrimaryKeySelective(orderItemDo);
			// 保存订单项扩展信息
			OrderItemExtendDo orderItemExtendDo = new OrderItemExtendDo();
			orderItemExtendDo.setObjectid(orderItem.id());
			orderItemExtendDo.setExtendcontext(orderItem.getGoods().toExtends());
			orderItemExtendDao.updateByPrimaryKeySelective(orderItemExtendDo);
			// 如果是套餐，需要保存个人购买套餐快照
			if (GoodsTypeEnum.COUPON.getCode() == orderItem.getGoods().getType()) {
				CouponGoods goods = (CouponGoods) orderItem.getGoods();
				BuyerCoupon buyerCoupon = new BuyerCoupon(order.getBuyer(), goods.getStartDate(), goods.getEndDate(),
						goods.getTimeZone(), order.id(), goods.getPark(), goods.getCanBuyGoodsTypes(),0,null,goods.goodsId());
				saveBuyerCoupon(buyerCoupon);
			}else if (GoodsTypeEnum.GAMECOUPON.getCode() == orderItem.getGoods().getType()){
				GameCouponGoods goods = (GameCouponGoods) orderItem.getGoods();
				BuyerCoupon buyerCoupon = new BuyerCoupon(order.getBuyer(), goods.getStartDate(), goods.getEndDate(),
						goods.getTimeZone(), order.id(), goods.getPark(), goods.getCanBuyGoodsTypes(),1,goods.getGame().id(),goods.goodsId());
				saveBuyerCoupon(buyerCoupon);
			}
		}
	}

	@Transactional
	@Override
	public void saveFilingOrder(Order order) {
		// 保存订单信息
//		OrderDo orderDo = new OrderDo();
//		orderDo.setObjectid(order.id());
//		orderDo.setUpdatetime(new Date());
//		orderDao.updateByPrimaryKeySelective(orderDo);
		Iterator<OrderItem> iterator = order.iterator();
		while (iterator.hasNext()) {
			// 保存订单项
			OrderItem orderItem = iterator.next();
			OrderItemDo orderItemDo = new OrderItemDo();
			orderItemDo.setObjectid(orderItem.id());
			orderItemDo.setUpdatetime(new Date());
			orderItemDo.setPreviewurl(orderItem.getPreviewUrl());
			orderItemDo.setDownloadurl(orderItem.getDownloadUrl());
			orderItemDo.setThumbnailurl(orderItem.getThumbnailUrl());
			orderItemDo.setStatus(orderItem.getFilingStatus().getCode());
			if (orderItem.getShareActivty() != null) {
				orderItemDo.setShareid(orderItem.getShareActivty().getShareId());
				orderItemDo.setParkshareid(orderItem.getShareActivty().getParkShareId());
				orderItemDo.setShareuploadflag(orderItem.getShareActivty().getUploadFlag());
				orderItemDo.setSharecheckurl(orderItem.getShareActivty().getShareCheckUrl());
			}
			orderItemDao.updateByPrimaryKeySelective(orderItemDo);
			// 保存订单项扩展信息
			OrderItemExtendDo orderItemExtendDo = new OrderItemExtendDo();
			orderItemExtendDo.setObjectid(orderItem.id());
			orderItemExtendDo.setExtendcontext(orderItem.getGoods().toExtends());
			orderItemExtendDao.updateByPrimaryKeySelective(orderItemExtendDo);
		}
	}

	@Transactional
	@Override
	public void saveShareActivityOrder(Order order) {
		// 保存订单信息
		OrderDo orderDo = new OrderDo();
		orderDo.setObjectid(order.id());
		orderDo.setUpdatetime(new Date());
		orderDao.updateByPrimaryKeySelective(orderDo);
		Iterator<OrderItem> iterator = order.iterator();
		while (iterator.hasNext()) {
			// 保存订单项
			OrderItem orderItem = iterator.next();
			if (orderItem.getShareActivty() != null) {
				OrderItemDo orderItemDo = new OrderItemDo();
				orderItemDo.setObjectid(orderItem.id());
				orderItemDo.setUpdatetime(new Date());
				orderItemDo.setShareid(orderItem.getShareActivty().getShareId());
				orderItemDo.setParkshareid(orderItem.getShareActivty().getParkShareId());
				orderItemDo.setShareuploadflag(orderItem.getShareActivty().getUploadFlag());
				orderItemDo.setShareurl(orderItem.getShareActivty().getShareUploadUrl());
				orderItemDo.setSharecheckurl(orderItem.getShareActivty().getShareCheckUrl());
				orderItemDao.updateByPrimaryKeySelective(orderItemDo);
			}
		}
	}

	private void onSaveCouponOrder(Order order) {
		if (order.getPromotionType().getCode() == PromotionTypeEnum.COUPON.getCode()) {
			Goods goods = null;
			BuyerCoupon buyerCoupon = order.getBuyerCoupon();
			if (buyerCoupon == null) {
				return;
			}
			for (OrderItem orderItem : order.getOrderItemList()) {
				goods = orderItem.getGoods();
				CouponOrder couponOrder = new CouponOrder(buyerCoupon, goods.id(),
						GoodsTypeEnum.valueOf(goods.getType()), order);
				saveCouponOrder(couponOrder);
			}
		}
	}

	private void onSaveOrder(Order order) {
		// 保存订单基本信息
		OrderDo orderDo = new OrderDo();
		orderDo.setOriginalamount(order.getOrderAmount().getOriginalAmount());
		orderDo.setActualamount(order.getOrderAmount().getActualAmount());
		orderDo.setPromotionamount(order.getOrderAmount().getPromotionAmount());
		orderDo.setActivityid(order.getActivity());
		// orderDo.setCurrency(order.getOrderAmount().getCurrency().toString());
		orderDo.setCount(Short.parseShort(order.getOrderItemCount().toString()));
		orderDo.setOrdertime(order.getPlaceOrderDate());
		orderDo.setObjectid(order.id());
		// orderDo.setOrderno(order.id());
		orderDo.setAmusementparkid(order.getPark().id());
		orderDo.setParkname(order.getPark().getParkName());
		Optional<Game> game = order.getGame();
		game.ifPresent(g -> {
			orderDo.setGameid(g.id());
			orderDo.setGamename(g.getGameName());
		});
		orderDo.setOriginalamount(order.getOrderAmount().getOriginalAmount());
		orderDo.setActualamount(order.getOrderAmount().getActualAmount());
		orderDo.setPromotionamount(order.getOrderAmount().getPromotionAmount());
		orderDo.setType(Short.parseShort(String.valueOf(order.getType())));
		orderDo.setOrdertype(Short.parseShort(String.valueOf(order.getPromotionType().getCode())));

		// orderDo.setPayway(order.getPayWay().getCode());
		orderDo.setPaymentid(order.getPaymentId().id());
		orderDo.setStatus(Short.parseShort(String.valueOf(order.getOrderStatus().getCode())));
		// orderDo.setTimezone(order.getTimeZone().getName());
		orderDo.setUsersid(order.getBuyer().id());
		// orderDo.setUsername(order.getBuyer().getUserName());
		// orderDo.setUpdatetime(order.getPlaceOrderDate());
		if (order.getParkShareActivity()!=null){
			orderDo.setShareid(order.getParkShareActivity().getShareId());
			orderDo.setParkshareid(order.getParkShareActivity().getParkShareId());
			short uploadFlag = order.getParkShareActivity().getUploadFlag()?(short) 1:0;
			orderDo.setShareuploadflag(uploadFlag);
		}

		if (order.getDevice() != null) {
			orderDo.setDevicerecordid(order.getDevice().getDeviceRecordId());
			orderDo.setDevicetype(order.getDevice().getDeviceType().getCode());
		}
		orderDo.setSearchid(order.getSearchId());
		orderDao.insertSelective(orderDo);

	}

	private void onSaveOrderItem(Order order) {

		Iterator<OrderItem> iterator = order.iterator();
		while (iterator.hasNext()) {
			// 保存订单项
			OrderItem next = iterator.next();
			OrderItemDo orderItemDo = new OrderItemDo();
			orderItemDo.setObjectid(next.id());
			orderItemDo.setOrderid(order.id());
			orderItemDo.setOriginalamount(next.getOriginalAmount());
			orderItemDo.setActualamount(next.getActualAmount());
			orderItemDo.setPromotiontype(order.getPromotionType().getCode());
			orderItemDo.setPromotionamount(next.getPromotionAmount());
			orderItemDo.setCurrency(order.getOrderAmount().getCurrency().toString());
			orderItemDo.setAmusementparkid(next.getGoods().getPark().id());
			orderItemDo.setParkname(next.getGoods().getPark().getParkName());
			orderItemDo.setCount(next.getCount());
			orderItemDo.setCreatetime(order.getPlaceOrderDate());
			orderItemDo.setDownloadurl(next.getDownloadUrl());
			orderItemDo.setPreviewurl(next.getPreviewUrl());
			orderItemDo.setThumbnailurl(next.getThumbnailUrl());
			orderItemDo.setGoodsid(next.getGoods().id());
			orderItemDo.setGoodsname(next.getGoods().getName());
			orderItemDo.setGoodstype(next.getGoods().getType());
			orderItemDo.setGoodsprice(next.getPrice());
			orderItemDo.setUserid(order.getBuyer().id());
			orderItemDo.setUsername(order.getBuyer().getUserName());
			orderItemDo.setUpdatetime(order.getPlaceOrderDate());
			orderItemDo.setStatus(next.getFilingStatus().getCode());

			if (!GoodsTypeEnum.PRINT1ADD1.getCode().equals(next.getGoods().getType())
					&& !GoodsTypeEnum.COUPON.getCode().equals(next.getGoods().getType())
					&& !GoodsTypeEnum.GAMECOUPON.getCode().equals(next.getGoods().getType())) {
				// 资源商品
				ResourceGoods resourceGoods = (ResourceGoods) next.getGoods();
				orderItemDo
						.setActivityid(resourceGoods.getActivity() == null ? null : resourceGoods.getActivity().id());
				orderItemDo.setGameid(resourceGoods.getGame().id());
				orderItemDo.setGamename(resourceGoods.getGame().getGameName());
				if (resourceGoods.getSeat()!=null){
					orderItemDo.setSeatid(resourceGoods.getSeat().id());
					orderItemDo.setSeatmark(resourceGoods.getSeat().getMark());
					orderItemDo.setSeatsequenceno(resourceGoods.getSeat().getSequenceNo());
				}

				orderItemDo.setResourcetype(resourceGoods.getResourceType());
				orderItemDo.setResourcename(resourceGoods.getResourceName());
				orderItemDo.setResourceCategory(resourceGoods.getResourceCategory().getCode());
			} else if (GoodsTypeEnum.PRINT1ADD1.getCode().equals(next.getGoods().getType())
					|| GoodsTypeEnum.GAMECOUPON.getCode().equals(next.getGoods().getType())){
				//PRINT1ADD1 和 GAMECOUPON 需要记录项目信息
				order.getGame().ifPresent(game -> {
					orderItemDo.setGameid(game.id());
					orderItemDo.setGamename(game.getGameName());
				});
			}

			if (next.getShareActivty()!=null){
				orderItemDo.setShareid(next.getShareActivty().getShareId());
				orderItemDo.setParkshareid(next.getShareActivty().getParkShareId());
				orderItemDo.setShareuploadflag(next.getShareActivty().getUploadFlag());
			}

			orderItemDao.insertSelective(orderItemDo);

			// 保存订单扩展信息
			OrderItemExtendDo orderItemExtendDo = new OrderItemExtendDo();
			orderItemExtendDo.setOrderid(order.id());
			orderItemExtendDo.setObjectid(next.id());
			orderItemExtendDo.setExtendcontext(next.getGoods().toExtends());
			orderItemExtendDo.setItemextendcontext(next.toExtend());
			orderItemExtendDao.insertSelective(orderItemExtendDo);
		}

	}

	@Override
	public void saveCouponOrder(CouponOrder couponOrder) {
		CouponOrderDo couponOrderDo = new CouponOrderDo();
		couponOrderDo.setObjectId(couponOrder.id());
		couponOrderDo.setCouponId(couponOrder.getCouponId());
		couponOrderDo.setCouponOrderId(couponOrder.getCouponOrderId());
		couponOrderDo.setCreateTime(new Date());
		couponOrderDo.setGoodsId(couponOrder.getGoodsId());
		couponOrderDo.setGoodsOrderId(couponOrder.getGoodsOrder().id());
		couponOrderDo.setGoodsType(couponOrder.getGoodsType().getCode());
		couponOrderDo.setStatus(EnumOrdersStatus.UN_PAID.getCode());
		couponOrderDo.setUserId(couponOrder.getUserId());
		couponOrderDo.setLevel(couponOrder.getLevel());
		couponOrderDao.insertSelective(couponOrderDo);
	}

	@Override
	public OrderDo getOrderById(String orderId) {
		return orderDao.selectByPrimaryKey(orderId);
	}
	@Override
	public Set<String> getGoodsIdsByUserPark(String buyer, String parkId, Integer goodsType, Date startDate,
			Date endDate) {
		return orderItemDao.getGoodsIdsByUserPark(buyer, parkId, goodsType, startDate, endDate);
	}

	@Override
	public OrderDo getOrderByUserId(String orderId, String userId) {
		return orderDao.getOrderByUser(orderId, userId);
	}
}