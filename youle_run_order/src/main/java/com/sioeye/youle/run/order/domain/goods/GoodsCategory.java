package com.sioeye.youle.run.order.domain.goods;

public enum GoodsCategory {
    BasicGoods(0),CombinedGoods(1);
    private Integer code;

    public Integer getCode() {
        return code;
    }
    GoodsCategory(Integer code){
        this.code = code;
    }
    public static GoodsCategory valueOf(Integer code){
        for (GoodsCategory goodsCategory:values()){
            if (goodsCategory.getCode().equals(code)){
                return goodsCategory;
            }
        }
        throw new RuntimeException(String.format("code(%s) can not convert to GoodsCategory.",code));
    }
    public static boolean equals(GoodsCategory source, GoodsCategory target){
        if (source == null || target == null){
            return false;
        }
        return source.getCode().equals(target.getCode());
    }

}
