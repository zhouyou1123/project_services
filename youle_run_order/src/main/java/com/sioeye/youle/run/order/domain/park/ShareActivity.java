package com.sioeye.youle.run.order.domain.park;

import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@Getter
public class ShareActivity  extends ValueObject {
    private String thirdShareId;
    private String platform;
    private Boolean isLocalDownload;
    private Boolean isShowShareLink;
    private Boolean isUpload;
    private String logoUrl;
    private String description;
    private String shareName;
    private String uploadApi;
    private String shareLinkApi;
}
