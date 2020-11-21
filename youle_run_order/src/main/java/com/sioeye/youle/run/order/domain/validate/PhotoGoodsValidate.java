package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.interfaces.RunCoreService;

import org.springframework.stereotype.Component;

@Deprecated
@Component
public class PhotoGoodsValidate implements ValidateDuplicateBuy, ValidateGoodsStatus {

	RunCoreService runCoreService;
	private BoughtService buyerCouponService;

	public PhotoGoodsValidate(BoughtService buyerCouponService, RunCoreService runCoreService) {
		this.runCoreService = runCoreService;
		this.buyerCouponService = buyerCouponService;
	}


	@Override
	public void validate(ValidateDuplicateBuyContext context) {
		// 验证用户照片是否已购买
		if (context.needValidate()) {
			validatePhotoGoodsDuplicateBuy(context.userId(), context.goodsId());
		}
	}

	private void validatePhotoGoodsDuplicateBuy(String userId, String goodsId) {
		if (buyerCouponService.checkIsBoughtGoods(userId, goodsId, GoodsTypeEnum.PHOTO.getCode()).isPresent()) {
			throw new CustomException(DomainErrorCodeEnum.DUPLICATE_BUY.getCode(),
					String.format(DomainErrorCodeEnum.DUPLICATE_BUY.getMessage(), goodsId));
		}
	}

	@Override
	public Goods getGoods(ValidateGoodsStatusContext context) {
		return runCoreService.getPhotoProcessDownUrl(context.goodsId());
	}
}
