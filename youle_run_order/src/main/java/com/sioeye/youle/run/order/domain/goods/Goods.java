package com.sioeye.youle.run.order.domain.goods;

import com.sioeye.youle.run.order.domain.common.AbstractId;
import lombok.Getter;

import java.util.Date;


@Getter
public abstract class Goods extends AbstractId implements GoodsContext {
    protected Park park;
    protected Game game;
    protected Date createDate;
    protected Integer type;
    protected String name;

    public Goods(String id, Integer type,String name, Park park){
        super(id);
        this.type = type;
        this.name = name;
        setPark(park);
    }
    private void setPark(Park park){
        assertArgumentNotNull(park,"the park of goods is not null.");
        this.park = park;
    }



    /**
     * 产品名称
     * @return
     */
    public String getName(){
        return name;
    }
    /**
     * 获取商品预览地址
     * @return
     */
    public abstract String getPreviewUrl();

    /**
     * 获取商品下载地址
     * @return
     */
    public abstract String getDownloadUrl();

    /**
     * 获取商品封面
     * @return
     */
    public abstract String getThumbnailUrl();

    /**
     * 验证商品是否过期（下架）
     */
    public abstract void validateOverdue();

    /**
     * 商品所属或者，如果没有活动就返回空
     * @return
     */
    public abstract Activity activity();

    /**
     * 生成扩展json信息
     * @return
     */
    public abstract String toExtends();

    /**
     * 根据资源地址重新构建商品
     * @param previewUrl
     * @param downloadUrl
     * @param thumbnailUrl
     * @return
     */
    public abstract Goods withResource(String previewUrl,String downloadUrl,String thumbnailUrl);





}
