/**  
* <p>Title: IAdminParkFeign.java</p>  
* <p>Description: </p>    
* @author GongGuowei  
* @date 2019年8月15日  
* @version 1.0  
*/  
package com.sioeye.youle.run.order.service.feign;

import java.util.Map;

import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.gateways.dto.DtoFeginResult;
import com.sioeye.youle.run.order.gateways.dto.DtoResult;
import com.sioeye.youle.run.order.gateways.request.ParkGoodsPriceDtoRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

/**
 * @Time: 2019年8月15日
 * @Description:
 */
@FeignClient(name="youle-admin-park-new")
public interface IAdminParkFeign {
	
	@RequestMapping(method=RequestMethod.POST, value="/park/get-price")
	public JSONObject getPrice(@RequestBody Map<String, String> map);
	@RequestMapping(method=RequestMethod.POST, value="/park/get-goods-price")
	public DtoResult<ParkGoodsPrice> getGoodsPrice(@RequestBody ParkGoodsPriceDtoRequest request);
}
