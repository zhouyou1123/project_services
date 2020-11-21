package com.sioeye.youle.run.order.gateways.dto;

import com.sioeye.youle.run.order.domain.goods.Park;
import com.sioeye.youle.run.order.domain.park.ParkShareActivity;
import com.sioeye.youle.run.order.domain.park.ShareActivity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@Data
public class ParkShareActivityDto {
    private String shareActivityId;
    private String parkId;
    private String parkName;
    private String timezone;
    private String currency;
    private String thirdShareId;
    private String logoUrl;
    private String description;
    private String shareName;
    private BigDecimal price;
    private String platform;
    private Boolean isLocalDownload;
    private Boolean isShowShareLink;
    private Boolean isUpload;
    private String uploadApi;
    private String shareLinkApi;
    private Date startTime;
    private Date endTime;
    private Collection<String> uploadGamesBlacklist;
    private Boolean enabled;
    private Boolean isInPeriod;
    private String summary;
//    private String description;

    public ParkShareActivity toParkShareActivity(){
        Park park = new Park(parkId,parkName);
        ShareActivity shareActivity = new ShareActivity(thirdShareId,platform,isLocalDownload,isShowShareLink,isUpload,logoUrl,description,shareName,uploadApi,shareLinkApi);
        return new ParkShareActivity(shareActivityId,park,timezone,currency,uploadGamesBlacklist,enabled,isInPeriod,startTime,endTime,price,shareActivity,summary,description);
    }
}
