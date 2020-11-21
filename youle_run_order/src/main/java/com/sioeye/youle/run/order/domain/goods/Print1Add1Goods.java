package com.sioeye.youle.run.order.domain.goods;

import java.util.Date;
import java.util.List;

public class Print1Add1Goods extends Goods {

    public Print1Add1Goods(String id,String name, Park park) {
        super(id, GoodsTypeEnum.PRINT1ADD1.getCode(),name,park);
    }

    @Override
    public String getPreviewUrl() {
        return null;
    }

    @Override
    public String getDownloadUrl() {
        return null;
    }

    @Override
    public String getThumbnailUrl() {
        return null;
    }

    @Override
    public void validateOverdue() {

    }

    @Override
    public Activity activity() {
        return null;
    }

    @Override
    public Goods withResource(String previewUrl, String downloadUrl, String thumbnailUrl) {
        return this;
    }

    @Override
    public String toExtends() {
        return "{}";
    }

    @Override
    public String goodsId() {
        return id();
    }

    @Override
    public Integer goodsType() {
        return getType();
    }
}
