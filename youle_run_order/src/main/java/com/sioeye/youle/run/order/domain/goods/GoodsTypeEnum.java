package com.sioeye.youle.run.order.domain.goods;

@Deprecated
public enum GoodsTypeEnum {
    /**
     * 机位小视频
     */
    CLIP(0),
    /**
     * 视频
     */
    VIDEO(1),
    /**
     * 集锦视频
     */
    HIGHLIGHT(2),
    /**
     * 图片
     */
    PHOTO(3),
    /**
     * 套票
     */
    COUPON(4),

    /**
     * 打印照片
     */
    PRINT(5),
    /**
     * 打印1+1
     */
    PRINT1ADD1(6),
    /**
     * 镜像视频
     */
    MIRRORCLIP(7),
    /**
     * 影集视频
     */
    GALLERYCLIP(8),
    /**
     * 分组小视屏
     */
    GROUPCLIP(9),
    /**
     * 项目小视屏
     */
    GAMECLIP(10),
    /**
     * 项目套餐
     */
    GAMECOUPON(11);

    private Integer code;

    public Integer getCode(){
        return code;
    }
    GoodsTypeEnum(Integer code){
        this.code=code;
    }

    public static GoodsTypeEnum valueOf(Integer code){
        for (GoodsTypeEnum type : values()){
            if (type.getCode()==code){
                return type;
            }
        }
        throw new RuntimeException(String.format("goodsType not support code:%s",code));
    }



    public static boolean equals(GoodsTypeEnum sourceGoodsType,GoodsTypeEnum targeGoodsType){
        if (sourceGoodsType == null || targeGoodsType ==null)return false;
        return sourceGoodsType.getCode().equals(targeGoodsType.getCode());

    }

}
