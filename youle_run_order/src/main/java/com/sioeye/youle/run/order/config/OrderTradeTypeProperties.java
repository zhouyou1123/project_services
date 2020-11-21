package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
@Data
@ConfigurationProperties(prefix = "order-trade-type")
@Configuration
public class OrderTradeTypeProperties {

	private List<OrderTradeType> tradeTypeList;

	private Stream<OrderTradeType> stream() {
		if (tradeTypeList == null) {
			return Stream.empty();
		}
		return tradeTypeList.stream();
	}

	public Integer getOrderType(EnumTradeType tradeType) {
		return stream().filter(trade -> trade.getTradeType().toString().equals(tradeType.toString()))
				.map(OrderTradeType::getOrderType).findAny()
				.orElseThrow(() -> new RuntimeException(String.format("%s get order type is error.", tradeType)));
	}

	public Boolean checkNeedValidateDuplicate(EnumTradeType tradeType) {
		return stream().filter(trade -> trade.getTradeType().toString().equals(tradeType.toString()))
				.map(OrderTradeType::getNeedValidateDuplicate).findAny().orElseThrow(
						() -> new RuntimeException(String.format("%s get needValidateDuplicate is error.", tradeType)));
	}

	public boolean checkNeedCalcAmount(EnumTradeType tradeType, GoodsTypeEnum goodsTypeEnum) {
		return stream().filter(trade -> trade.getTradeType().toString().equals(tradeType.toString()))
				.map(trade -> trade.checkNeedCalcAmount(goodsTypeEnum)).findAny()
				.orElseThrow(() -> new RuntimeException(String.format("%s not contain %s.", tradeType, goodsTypeEnum)));
	}

	public void checkGoodsTypeError(EnumTradeType tradeType, List<GoodsTypeEnum> goodsTypeList) {
		// 判断是否缺少参数
		stream().filter(trade -> trade.getTradeType().toString().equals(tradeType.toString())).findAny()
				.ifPresent(trade -> {
					trade.checkLackGoodsType(goodsTypeList);
				});
		// 判断是否有多余的无效参数
		stream().filter(trade -> trade.getTradeType().toString().equals(tradeType.toString())).findAny()
				.ifPresent(trade -> {
					trade.checkInvalidGoodsType(goodsTypeList);
				});
	}

	public String getPaymentDescription(EnumTradeType enumTradeType) {
		Optional<OrderTradeType> optional = stream().filter(trade -> trade.getTradeType() == enumTradeType).findAny();
		if (optional.isPresent()) {
			return optional.get().getPaymentDescription();
		}
		return null;
	}
}
