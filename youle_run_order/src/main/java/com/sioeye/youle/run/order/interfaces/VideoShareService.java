package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.goods.ClipGoods;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年11月21日
 *
 * @fileName VideoShareService.java
 *
 * @todo 视频分享
 */
public interface VideoShareService {

	public boolean uploadWeishi(String uploadApi, String shareCheckApi, String orderId, ClipGoods clipGoods);
}
