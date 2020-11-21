package com.sioeye.youle.run.order.domain.park;

import com.sioeye.youle.run.order.domain.goods.Park;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class ParkShareActivity {
    private String shareActivityId; // 游乐园分享活动ID*
    private Park park;
    private String timezone;
    private String currency;
    private Collection<String> uploadGamesBlacklist;
    private Boolean enabled;
    private Boolean isInPeriod;
    private Date startTime;
    private Date endTime;
    private BigDecimal sharePrice;
    private ShareActivity shareActivity;
    private String summary;
    private String description;

    public boolean checkGameCanUploadShare(String gameId){

        if (!this.enabled || !isInPeriod){
            //没有启用或者不在有效期内，不能上传
            return false;
        }
        //判断上传总开关
        if(shareActivity == null || shareActivity.getIsUpload() == null || !shareActivity.getIsUpload()) {
        	return false;
        }
        if (uploadGamesBlacklist==null || uploadGamesBlacklist.size()<1){
            //没有项目黑名单，就都可以上传
            return true;
        }
        //项目黑名单，不包含参数（gameId）就上传
        return !uploadGamesBlacklist.contains(gameId);
    }
}
