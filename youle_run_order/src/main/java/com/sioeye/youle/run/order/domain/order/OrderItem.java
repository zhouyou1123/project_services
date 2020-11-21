package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.domain.common.AbstractId;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

@Getter
public class OrderItem extends AbstractId {

	private Date createDate;
	/**
	 * 原始单价
	 */
	private BigDecimal price;
	private BigDecimal actualAmount;
	private int count;
	private String previewUrl;
	private String downloadUrl;
	private String thumbnailUrl;
	private FilingStatusEnum filingStatus;
	private OrderItemShareActivty shareActivty;
	private Goods goods;
	private PriceFunction priceFunction;
	private BiFunction<GoodsContext, BigDecimal, BigDecimal> actualAmountFunction;
	private Boolean needCalcAmount;

	// public static OrderItem createItem(Goods goods, BigDecimal price, Integer
	// count, BigDecimal actualAmount,OrderItemShareActivty shareActivty, Date
	// createDate){
	// return new
	// OrderItem(OrderIdUtils.generatorOrderItemId(),goods,price,count,actualAmount,null,goods.getPreviewUrl(),goods.getDownloadUrl(),goods.getThumbnailUrl(),
	// FilingStatusEnum.NOTFILING,createDate);
	// }
	public static OrderItem createItem(Goods goods, Integer count, OrderItemShareActivty shareActivty, Date createDate,
			PriceFunction priceFunction, BiFunction<GoodsContext, BigDecimal, BigDecimal> actualAmountFunction,
			Boolean needCalcAmount) {
		return new OrderItem(OrderIdUtils.generatorOrderItemId(), goods, null, count, null, shareActivty, goods.getPreviewUrl(),
				goods.getDownloadUrl(), goods.getThumbnailUrl(), FilingStatusEnum.NOTFILING, createDate, priceFunction,
				actualAmountFunction, needCalcAmount);
	}

	private OrderItem(String itemId, Goods goods, BigDecimal price, Integer count, BigDecimal actualAmount,
			OrderItemShareActivty shareActivty, String previewUrl, String downloadUrl, String thumbnailUrl,
			FilingStatusEnum filingStatus, Date createDate, PriceFunction priceFunction,
			BiFunction<GoodsContext, BigDecimal, BigDecimal> actualAmountFunction, Boolean needCalcAmount) {
		super(itemId);
		setPrice(price, priceFunction);
		setActualAmount(actualAmount, actualAmountFunction);
		setCount(count);
		setCreateDate(createDate);
		setGoods(goods);
		this.previewUrl = previewUrl;
		this.downloadUrl = downloadUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.filingStatus = filingStatus;
		this.shareActivty = shareActivty;
		this.needCalcAmount = needCalcAmount;

	}

	public OrderItem(String itemId, Goods goods, BigDecimal price, Integer count, BigDecimal actualAmount,
			OrderItemShareActivty shareActivty, String previewUrl, String downloadUrl, String thumbnailUrl,
			FilingStatusEnum filingStatus, Date createDate) {
		this(itemId, goods, price, count, actualAmount, shareActivty, previewUrl, downloadUrl, thumbnailUrl,
				filingStatus, createDate, null, null, false);
	}

	public void increment(int count, BigDecimal actualAmount) {
		if (count < 1) {
			throw new IllegalArgumentException("the count of order item is greater than 0.");
		}

		this.count += count;
		this.actualAmount.add(actualAmount);
	}

	public void updateShareActivity(OrderItemShareActivty orderItemShareActivty) {
		this.shareActivty = orderItemShareActivty;
	}

	public BigDecimal getPrice() {
		if (price == null) {
			price = priceFunction.apply(getGoods().getPark().id(),
					getGoods().getGame() == null ? null : getGoods().getGame().id(), goods.getType());
		}
		return price;
	}

	public BigDecimal getActualAmount() {
		if (actualAmount == null) {
			if (needCalcAmount) {
				actualAmount = actualAmountFunction.apply(getGoods(), getPrice());
				return actualAmount;
			}
			return BigDecimal.ZERO;
		}
		return actualAmount;
	}

	/**
	 * 开始归档，保存永久地址
	 * 
	 * @param previewFinalUrl
	 * @param downloadFinalUrl
	 * @param thumbnailFinalUrl
	 */
	public void beginFiling(String previewFinalUrl, String downloadFinalUrl, String thumbnailFinalUrl) {
		this.filingStatus = FilingStatusEnum.FILING;
		goods = goods.withResource(previewFinalUrl, downloadFinalUrl, thumbnailFinalUrl);

	}

	/**
	 * 交换临时地址和永久地址
	 */
	public void endFiling() {
		this.filingStatus = FilingStatusEnum.AREADYFILING;
		String switchPreviewUrl = this.previewUrl;
		String switchDownloadUrl = this.downloadUrl;
		String switchThumbnailUrl = this.thumbnailUrl;
		this.setDownloadUrl(goods.getDownloadUrl());
		this.setPreviewUrl(goods.getPreviewUrl());
		this.setThumbnailUrl(goods.getThumbnailUrl());
		goods = goods.withResource(switchPreviewUrl, switchDownloadUrl, switchThumbnailUrl);
	}

	/**
	 * 获取原始总价格， 数量*原始单价格
	 * 
	 * @return
	 */
	public BigDecimal getOriginalAmount() {
		return getPrice().multiply(BigDecimal.valueOf(count));
	}

	/**
	 * 获取订单项的优惠金额
	 * 
	 * @return
	 */
	public BigDecimal getPromotionAmount() {
		return getOriginalAmount().subtract(getActualAmount());
	}

	private void setPrice(BigDecimal price, PriceFunction priceFunction) {
		if (price == null && priceFunction == null) {
			throw new RuntimeException("the price of orderitem is not null.");
		}
		this.price = price;
		this.priceFunction = priceFunction;
	}

	private void setCount(Integer count) {
		assertArgumentNotNull(count, "the count of orderitem is not null and greater then 0.");
		this.count = count;
	}

	private void setCreateDate(Date createDate) {
		assertArgumentNotNull(createDate, "the create date of orderitem is not null and greater then 0.");
		this.createDate = createDate;
	}

	private void setActualAmount(BigDecimal actualAmount,
			BiFunction<GoodsContext, BigDecimal, BigDecimal> actualAmountFunction) {
		if (actualAmount == null && actualAmountFunction == null) {
			throw new RuntimeException("the actual amount  of orderitem is not null.");
		}
		this.actualAmount = actualAmount;
		this.actualAmountFunction = actualAmountFunction;
	}

	private void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	private void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	private void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	private void setGoods(Goods goods) {
		assertArgumentNotNull(goods, "goods is not null.");
		this.goods = goods;
	}

	public String toExtend(){
		if (shareActivty==null){
			return "{}";
		}else{
			return "{" +
					"\"shareUploadUrl\":\"" + shareActivty.getShareUploadUrl() +"\"," +
					"\"shareCheckUrl\":\"" + shareActivty.getShareCheckUrl()+"\""+
					"}";
		}
	}

}
