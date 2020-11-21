package com.sioeye.youle.run.order.domain.goods;

import lombok.Getter;


@Deprecated
@Getter
public class ClipGoods extends Goods {

    private Seat seat;
    private Activity activity;
    private Clip clip;


    public ClipGoods(String id, Park park, Game game, Seat seat, Activity activity, Clip clip) {
        super(id, GoodsTypeEnum.CLIP.getCode(),"CLIP",park);
        setGame(game);
        setSeat(seat);
        setActivity(activity);
        setClip(clip);
    }

    private void setClip(Clip clip){
        assertArgumentNotNull(clip,"the url of clip is not null.");
        this.clip = clip;
    }

    private void setActivity(Activity activity){
        assertArgumentNotNull(activity,"the activity of clip is not null.");
        this.activity = activity;
    }

    private void setSeat(Seat seat){
        assertArgumentNotNull(seat,"the seat of clip is not null.");
        this.seat = seat;
    }

    private void setGame(Game game){
        assertArgumentNotNull(game,"the game of clip is not null.");
        this.game = game;
    }

    @Override
    public String getPreviewUrl() {

        return clip.getPreviewUrl();
    }

    @Override
    public String getDownloadUrl() {

        return clip.getDownloadUrl();
    }

    @Override
    public String getThumbnailUrl() {

        return clip.getThumbnailUrl();
    }

    @Override
    public void validateOverdue() {

    }

    @Override
    public Activity activity() {
        return activity;
    }

    @Override
    public String toExtends() {
//        map.getDate("startDate"),map.getInteger("size"),map.getInteger("duration"),map.getInteger("width"),
//                map.getInteger("height"));
//        priviewUrl,downloadUrl,thumbnailUrl
        //序列化扩展信息
        String thumbnail = this.getClip().getThumbnailUrl()==null?"":this.getClip().getThumbnailUrl();
        return "{"+
                "\"previewUrl\":\""+this.getClip().getPreviewUrl()+"\","+
                "\"downloadUrl\":\""+this.getClip().getDownloadUrl()+"\","+
                "\"thumbnailUrl\":\""+thumbnail+"\","+
                "\"startDate\":"+this.clip.getStartTime().getTime()+","+
                "\"size\":"+this.clip.getSize()+","+
                "\"duration\":"+this.getClip().getDuration()+","+
                "\"width\":"+this.getClip().getSize()+","+
                "\"height\":"+this.getClip().getHeight()+
                "}";
    }

    @Override
    public Goods withResource(String previewUrl, String downloadUrl, String thumbnailUrl) {
        Clip clip = new Clip(downloadUrl,previewUrl,thumbnailUrl,this.getClip().getStartTime(),this.getClip().getSize(),
                this.getClip().getDuration(),this.getClip().getWidth(),this.getClip().getHeight());
        return new ClipGoods(this.id(),this.getPark(),this.getGame(),
                this.getSeat(),this.getActivity(),clip);
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
