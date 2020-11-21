package com.sioeye.youle.run.order.context;

import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderItem;

import java.util.stream.Collectors;

public class PersonalOrderDetailConverter {
//    public PersonalOrderResourceResponse convertToDetail(Order order){
//        PersonalOrderResourceResponse orderDetailResponse = new PersonalOrderResourceResponse();
//        orderDetailResponse.setActualAmount(order.getOrderAmount().getActualAmount());
//        orderDetailResponse.setCurrency(order.getOrderAmount().getCurrency());
//        orderDetailResponse.setOpenId(order.getBuyer().getOpenId());
//        orderDetailResponse.setOrderId(order.id());
//        orderDetailResponse.setOrderNo(order.getOrderNo());
//        orderDetailResponse.setOrderStatus(order.getOrderStatus());
//        orderDetailResponse.setOriginalAmount(order.getOrderAmount().getOriginalAmount());
//        orderDetailResponse.setPaymentDate(order.getPaymentDate());
//        orderDetailResponse.setUserId(order.getBuyer().id());
//        orderDetailResponse.setPayWay(order.getPayWay());
//        orderDetailResponse.setPlaceOrderDate(order.getPlaceOrderDate());
//        orderDetailResponse.setOrderItem(order.stream().map(item->convertToDetail(item)).collect(Collectors.toList()));
//        return orderDetailResponse;
//    }
//    public PersonalOrderItemResourceResponse convertToDetail(OrderItem orderItem){
//
//        PersonalOrderItemResourceResponse orderItemDetailResponse = new PersonalOrderItemResourceResponse();
//        orderItemDetailResponse.setActualAmount(orderItem.getActualAmount());
//        orderItemDetailResponse.setCount(orderItem.getCount());
//        orderItemDetailResponse.setCreateDate(orderItem.getCreateDate());
//        orderItemDetailResponse.setDownloadUrl(orderItem.getDownloadUrl());
//        orderItemDetailResponse.setFilingStatus(orderItem.getFilingStatus());
//        orderItemDetailResponse.setActualAmount(orderItem.getActualAmount());
//        orderItemDetailResponse.setGoodsId(orderItem.getGoods().id());
//        orderItemDetailResponse.setOrderItemId(orderItem.id());
//        orderItemDetailResponse.setPreviewUrl(orderItem.getPreviewUrl());
//        orderItemDetailResponse.setPrice(orderItem.getPrice());
//        orderItemDetailResponse.setThumbnailUrl(orderItem.getThumbnailUrl());
//        orderItemDetailResponse.setType(orderItem.getGoods().getType());
//        orderItemDetailResponse.setDownloadUrl(objectStorageService.getSignUrl(orderItem.getFilingStatus().getCode(),orderItem.getDownloadUrl())) ;
//        return orderItemDetailResponse;
//    }
}
