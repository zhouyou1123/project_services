package com.sioeye.youle.run.order.domain.goods;

import lombok.Getter;

@Deprecated
@Getter
public class PrintGoods extends Goods {

    private Seat seat;
    private Activity activity;
    private String previewUrl;
    private String downloadUrl;

    public PrintGoods(String id, Park park, Game game, Seat seat, Activity activity, String previewUrl, String downloadUrl) {
        super(id, GoodsTypeEnum.PRINT.getCode(),"PRINT",park);
        this.setGame(game);
        this.setSeat(seat);
        this.setActivity(activity);
        this.setPreviewUrl(previewUrl);
        this.setDownloadUrl(downloadUrl);

    }
    private void setActivity(Activity activity){
        assertArgumentNotNull(activity,"the activity of print photo is not null.");
        this.activity = activity;
    }
    private void setPreviewUrl(String previewUrl){
//        assertArgumentNotEmpty(previewUrl,"the preview url of print photo is not null.");
        this.previewUrl = previewUrl;
    }
    private void setDownloadUrl(String downloadUrl){
//        assertArgumentNotEmpty(downloadUrl,"the download url of print photo is not null.");
        this.downloadUrl = downloadUrl;
    }
    private void setGame(Game game){
        assertArgumentNotNull(game,"the game of print photo is not null.");
        this.game = game;
    }
    private void setSeat(Seat seat){
        assertArgumentNotNull(seat,"the seat of print photo is not null.");
        this.seat = seat;
    }

    @Override
    public String getPreviewUrl() {
        return this.previewUrl;
    }

    @Override
    public String getDownloadUrl() {
        return this.downloadUrl;
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
        return activity;
    }
    @Override
    public Goods withResource(String previewUrl, String downloadUrl, String thumbnailUrl) {
        return  new PrintGoods(this.id(),this.getPark(),this.getGame(),
                this.getSeat(),this.getActivity(),previewUrl,downloadUrl);
    }

    @Override
    public String toExtends() {
        //priviewUrl,downloadUrl
        //序列化扩展内容
        return "{"+
                "\"previewUrl\":\""+this.previewUrl+"\","+
                "\"downloadUrl\":\""+this.downloadUrl+"\","+
                "}";
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
