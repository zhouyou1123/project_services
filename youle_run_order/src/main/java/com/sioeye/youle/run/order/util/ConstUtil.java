package com.sioeye.youle.run.order.util;

import java.math.BigDecimal;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年1月16日
 *
 * @fileName ConstUtil.java
 *
 * @todo 常量工具类
 */
public class ConstUtil {

	public static final String X_YOULE_FLAG = "1";
	public static final String X_YOULE_TYPE = "1";
	public static final String X_YOULE_PARKID = "sioeye";
	public static final int USER_VIDEOBOUGHT_STATUS = 1;
	public static final int USER_MERGEVIDEO_MERGING_STATUS = 1;
	public static final Double PARK_HIGHLIGHT_PRICE_DEFAULT = 0d;
	public static final Double PARK_PHOTO_PRICE_DEFAULT = 0d;
	public static final Double PRINT_PHOTO_PRICE_DEFAULT = 0d;
	public static final String DISNEY_FREE_PAYMENT = "disney_free_payment";

	// s3地址类型0：临时1：永久
	public static final int S3_ADDR_TMP = 0;
	public static final int S3_ADDR_AEON = 1;
	public static final int HIGHLIGHT_CLIPS_MIN = 2;
	public static final int HIGHLIGHT_CLIPS_MAX = 6;
	// 下单类型--小视频
	public static final int ORDER_TYPE_CLIP = 0;
	// 下单类型--集锦视频
	public static final int ORDER_TYPE_HIGHLIGHT = 2;
	// 下单类型--照片
	public static final int ORDER_TYPE_PHOTO = 3;
	// 下单类型--套餐
	public static final int ORDER_TYPE_COUPON = 4;
	// 使用套餐购买小视频的价格，就为0
	public static final BigDecimal USE_COUPON_BUY_CLIP_PRICE = new BigDecimal(0);

	public static final int NOTIFY_TYPE = 2;
	public static final String NOTIFY_ROUTING_KEY = "2";

	public static final String PHOTOPRINT_PAY_KEY = "photoprint:";
	public static final String ORDER_STATUS_KEY = "orderstatus:";

	// 小视频、照片、照片打印支付状态
	public static final int STATUS_NOT_MOVE = 1;// 已支付未移动
	public static final int STATUS_MOVE = 2;// 已支付已移动

	public static final int GOODS_TYPE_CLIP = 0;// 商品类型-小视频
	public static final int GOODS_TYPE_COUPON = 4;// 商品类型-套餐
	public static final int GOODS_TYPE_PHOTO = 3;// 商品类型-电子照片
	public static final int GOODS_TYPE_PHOTOPRINT = 5;// 商品类型-打印照片

//	public static final String ORDER_STATUS_WEIXIN = "weixin"; //微信小程序查询订单状态
}
