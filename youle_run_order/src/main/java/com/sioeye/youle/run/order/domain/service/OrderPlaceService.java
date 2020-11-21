package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.config.ResourceTypeProperties;
import com.sioeye.youle.run.order.context.CreateOrderRequest;
import com.sioeye.youle.run.order.context.CreateOrderResponse;
import com.sioeye.youle.run.order.context.OrderGoods;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.FilingStatusEnum;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.validate.ValidateFactory;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.interfaces.NotificationService;
import com.sioeye.youle.run.order.interfaces.RunCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OrderPlaceService {


    @Autowired
    private ValidateOrderDuplicateBuyService duplicateBuyService;

    @Autowired
    private ValidateGoodsStatusService goodsStatusService;
    @Autowired
    private ValidateOrderAmountService validateOrderAmountService;
    @Autowired
    private AdminParkService parkService;
    @Autowired
    private RunCoreService coreService;
    @Autowired
    private CreateOrderPaymentService createOrderPaymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CopyGoodsResourceToFinalBucketService finalBucketService;
    @Autowired
    private PromotionActivityService promotionActivityService;
    @Autowired
    private ValidateFactory validateFactory;

    @Autowired
    private ResourceTypeProperties resourceTypeProperties;

    @Autowired
    private NotificationService notificationService;


    public CreateOrderResponse placeOrderNew(CreateOrderRequest orderRequest) {
        //扩展订单的商品列表
        extendGoodsList(orderRequest);
        //获取 OrderContext 对象
        OrderContext orderContext = new OrderContext(orderRequest, parkService::getParkGoodsPrice,
                (promotionContext, goodsContext, parkGoodsPrice, orderContext1) -> validateFactory.getValidateGoodsPromotion(promotionContext.getPromotionType()).calculatePrice(goodsContext, parkGoodsPrice, orderContext1, promotionContext));
        //验证是否重复购买
        duplicateBuyService.validateDuplicateBuy(orderContext);
        //验证商品状态
//        goodsStatusService.validateStatus(orderContext);
        //验证金额
        validateOrderAmountService.validateAmount(orderContext);
        //创建支付记录
        createOrderPaymentService.createOrderPayment(orderContext::toPayment, orderContext::applyPaymentResult);
        //保存订单信息
        orderRepository.saveOrder(orderContext.toOrder());
        //发送已下单事件通知
        notificationService.sendNotification(orderContext.toPlacedOrderEvent());
        //构建返回值
        return orderContext.toResponse();
    }

    public void paidBack(String orderId) {
        //获取订单信息
        Order order = orderRepository.getOrder(orderId);
        if (OrderStatusEnum.NOT_PAY.equals(order.getOrderStatus())) {
            order.paidBack(new Date());
            // TODO 复制商品到S3永久桶
            finalBucketService.copy(order);
            //保存订单支付信息
            orderRepository.savePaidOrder(order);
            // TODO 分享活动
            promotionActivityService.share(order);
        } else {
            log.info("{" +
                    "\"orderId\":\"" + order.id() + "\"," +
                    "\"status\":\"" + order.getOrderStatus() + "\"," +
                    "\"message\":\"duplicate pay back.\"" +
                    "}");
        }

    }


    public void filingBack(String orderId) {
        //获取订单信息
        Order order = orderRepository.getOrder(orderId);
        if (FilingStatusEnum.FILING.getCode() == order.getFirstOrderItem().getFilingStatus().getCode()) {
            order.filingBack();
            orderRepository.saveFilingOrder(order);
        } else {
            log.info("{" +
                    "\"orderId\":\"" + order.id() + "\"," +
                    "\"status\":\"" + order.getOrderStatus() + "\"," +
                    "\"message\":\"duplicate filing back.\"" +
                    "}");
        }

    }

    private void extendGoodsList(CreateOrderRequest orderRequest) {
        //TODO 根据传入的商品列表的第一个商品类型来决定是否扩展商品列表
        Integer goodsType = orderRequest.getGoodsList().get(0).getGoodsType();
        if (goodsType == null) {
            //不需要扩展
            return;
        }
        if (goodsType.equals(GoodsTypeEnum.PRINT1ADD1.getCode())) {
            Optional<OrderGoods> photoGoodsOptional = orderRequest.getGoodsList().stream().filter(g -> g.getResourceType() != null).
                    filter(g -> resourceTypeProperties.containsPhotoResourceType(g.getResourceType())).findAny();
            if (photoGoodsOptional.isPresent()) {
                //根据定点照片获取定点小视频
                OrderGoods photoGoods = photoGoodsOptional.get();
                CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> coreService.getClipIdByPhotoId(photoGoods.getGoodsId()));
                //TODO 应该是资源服务返回 具体是机位视频还是机位镜像视频
                if (resourceTypeProperties.getPhoto().get(0) == photoGoods.getResourceType()){
                    orderRequest.getGoodsList().add(new OrderGoods(resourceTypeProperties.getSeatClip().get(0), ResourceCategory.Video, completableFuture));
                }else{
                    orderRequest.getGoodsList().add(new OrderGoods(resourceTypeProperties.getSeatClip().get(1), ResourceCategory.Video, completableFuture));
                }
                //添加打印照片的资源类型和资源分类
                orderRequest.getGoodsList().add(new OrderGoods(photoGoods.getGoodsId(),GoodsTypeEnum.PRINT.getCode(),photoGoods.getResourceType(),ResourceCategory.Photo,1,null));
            }
        }
        if (goodsType.equals(GoodsTypeEnum.PRINT.getCode())){
            Optional<OrderGoods> photoGoodsOptional = orderRequest.getGoodsList().stream().filter(g -> g.getResourceType() != null).
                    filter(g -> resourceTypeProperties.containsPhotoResourceType(g.getResourceType())).findAny();
            if (photoGoodsOptional.isPresent()) {
                //设置打印照片的资源类型和资源分类
                OrderGoods photoGoods = photoGoodsOptional.get();
                orderRequest.getGoodsList().stream().filter(g->g.goodsType()!=null).filter(g->g.goodsType()==GoodsTypeEnum.PRINT.getCode()).findAny().ifPresent(g->{
                    g.setGoodsId(photoGoods.getGoodsId());
                    g.setResourceType(photoGoods.getResourceType());
                    g.setResourceCategory(photoGoods.getResourceCategory());
                });
            }
        }
    }


}
