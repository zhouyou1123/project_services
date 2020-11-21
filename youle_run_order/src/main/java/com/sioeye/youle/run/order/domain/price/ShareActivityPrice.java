package com.sioeye.youle.run.order.domain.price;

import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class ShareActivityPrice extends ValueObject{
    private String thirdShareId; // 第三方分享配置ID
    private String shareActivityId; // 游乐园分享活动ID
    private String logoUrl;
    private String description;
    private String shareName;
    private BigDecimal sharePrice = BigDecimal.ZERO;
    private String platform;
    private Boolean isLocalDownload;
    private Boolean isShowShareLink;
    private Boolean isUpload;
    private String uploadApi;
    private String shareLinkApi;
    private Date startTime;
    private Date endTime;
    @JSONField(serialize = false,deserialize = false)
    private Collection<String> uploadGamesBlacklist;
    private Boolean enabled;
    private Boolean isInPeriod;
 	private String summary;
    public ShareActivityPrice(){}

    public boolean checkGameCanUploadShare(String gameId){

        if (!this.enabled || !isInPeriod){
            //没有启用或者不在有效期内，不能上传
            return false;
        }
        if (uploadGamesBlacklist==null || uploadGamesBlacklist.size()<1){
            //没有项目黑名单，就都可以上传
            return true;
        }
        //项目黑名单，不包含参数（gameId）就上传
        return !uploadGamesBlacklist.contains(gameId);
    }
    public boolean canUseShareActivity(){
        if (!getEnabled()) {
            throw new CustomException(EnumHandle.SHARE_ACTIVITY_IS_NOT_ENABLE);
        } else if (!getIsInPeriod()) {
            throw new CustomException(EnumHandle.SHARE_ACTIVITY_IS_NOT_VALID);
        }
        return true;
    }

	public void setUploadApi(String uploadApi) {
		this.uploadApi = uploadApi;
	}


}
