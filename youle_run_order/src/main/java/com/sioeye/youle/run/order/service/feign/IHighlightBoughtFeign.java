package com.sioeye.youle.run.order.service.feign;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月5日
 *
 * @fileName IVideoBoughtFeign.java
 *
 * @todo
 */
@FeignClient(name = "youle-run-highlight")
public interface IHighlightBoughtFeign {

	/**
	 * 更新视频集锦状态
	 * 
	 * @param map
	 * @param tokenUserId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/highlight/update_highlightbought_status")
	public String updateHighlightBoughtStatus(@RequestBody Map<String, Object> map);

	/**
	 * 获取集锦下载地址
	 * 
	 * @param map
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/highlight/get_order_highlighturl")
	public String getOrderHighlightUrl(@RequestBody Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/highlight/add_highlight")
	public String addHighlight(@RequestBody Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/highlight/update_order_highlight")
	public String updateOrderHighlight(@RequestBody Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return
	 * String
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/highlight/get_highlightbought_by_order_id")
	public String getHighlightBoughtByOrderId(@RequestBody Map<String, Object> map);
}
