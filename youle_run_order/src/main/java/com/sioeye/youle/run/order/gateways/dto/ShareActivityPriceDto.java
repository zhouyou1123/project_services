package com.sioeye.youle.run.order.gateways.dto;

import com.sioeye.youle.run.order.domain.price.ShareActivityPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@Data
public class ShareActivityPriceDto {
	private String shareActivityId;
	private String thirdShareId;
	private String logoUrl;
	private String description;
	private String shareName;
	private BigDecimal sharePrice;
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

	public ShareActivityPrice toShareActivityPrice() {
		return new ShareActivityPrice(thirdShareId, shareActivityId, logoUrl, description, shareName, sharePrice,
				platform, isLocalDownload, isShowShareLink, isUpload, uploadApi, shareLinkApi, startTime, endTime,
				uploadGamesBlacklist, enabled, isInPeriod, summary);
	}
}
