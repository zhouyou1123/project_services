package com.sioeye.youle.run.order.domain.price;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.sioeye.youle.run.order.domain.goods.GoodsCategory;

import lombok.Data;

@Data
public class ParkGoodsPrice {
	private String currency;
	private Boolean enabled;
	private String parkId;
	private String parkName;
	private Boolean stopped;
	private String timeZone = "Asia/Shanghai";
	List<GoodsPriceConfiguration> priceList;

	public static void main(String[] args) {
		ParkGoodsPrice.test(null, null);
	}

	public static void test(String parkName, String gameName) {
		StringBuffer paymentDescription = new StringBuffer();
		Optional.ofNullable(parkName).ifPresent(name -> {
			paymentDescription.append("-");
			paymentDescription.append(name);
		});
		Optional.ofNullable(gameName).ifPresent(name -> {
			paymentDescription.append("-");
			paymentDescription.append(name);
		});
		System.out.println(paymentDescription.toString());
	}

	public Optional<GoodsPriceConfiguration> getGoodsPriceConfig(Integer goodsType) {
		if (priceList == null || priceList.size() < 1) {
			return null;
		}
		return priceList.stream().filter(price -> price.getGoodsPrice().getType().equals(goodsType)).findAny();
	}

	public Optional<GoodsPriceConfiguration> getGoodsPriceConfigByResourceType(Integer resourceType) {
		if (priceList == null || priceList.size() < 1) {
			return null;
		}
		return priceList.stream()
				.filter(price -> GoodsCategory.equals(GoodsCategory.BasicGoods,
						price.getGoodsPrice().getGoodsCategory()))
				.filter(price -> price.getGoodsPrice().getSaleConfig() != null
						&& price.getGoodsPrice().getSaleConfig().getValidResource() != null)
				.filter(price -> {
					return Arrays.stream(price.getGoodsPrice().getSaleConfig().getValidResource())
							.flatMap(resource -> Arrays.stream(resource))
							.filter(resource -> resource.equals(resourceType)).findAny().isPresent();
				}).findAny();
	}

	public Optional<GoodsPrice> getGoodsPrice(Integer goodsType) {
		return getGoodsPriceConfig(goodsType).map(GoodsPriceConfiguration::getGoodsPrice);
	}

	public Optional<GoodsPrice> getGoodsPriceByResourceType(Integer resourceType) {
		return getGoodsPriceConfigByResourceType(resourceType).map(GoodsPriceConfiguration::getGoodsPrice);
	}

	public Optional<ShareActivityPrice> getShareActivityPrice(Integer goodsType, String shareActivityId) {
		return getPriceList().stream()
				.filter(goodsPriceConfiguration -> goodsPriceConfiguration.getGoodsPrice().getGoodsType() == goodsType)
				.filter(goodsPriceConfiguration -> goodsPriceConfiguration.getSharePrice() != null)
				.flatMap(goodsPriceConfiguration -> goodsPriceConfiguration.getSharePrice().stream())
				.filter(shareActivity -> shareActivity.getShareActivityId() != null
						&& shareActivity.getShareActivityId().equals(shareActivityId))
				.findAny();
	}

}
