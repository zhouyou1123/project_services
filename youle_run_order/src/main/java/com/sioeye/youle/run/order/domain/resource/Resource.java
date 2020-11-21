package com.sioeye.youle.run.order.domain.resource;

import com.sioeye.youle.run.order.domain.goods.Activity;
import com.sioeye.youle.run.order.domain.goods.Game;
import com.sioeye.youle.run.order.domain.goods.Park;
import com.sioeye.youle.run.order.domain.goods.Seat;

import java.util.Date;
import java.util.Optional;

public interface Resource {
	String getResourceId();

	String getResourceName();

	ResourceCategory getResourceCategory();

	Integer getResourceType();

	String getParkId();

	String getParkName();

	String getGameId();

	String getGameName();

	String getSeatId();

	String getSeatName();

	String getSeatSequenceNo();

	String getThumbnailUrl();

	String getPreviewUrl();

	String getDownloadUrl();

	Integer getWidth();

	Integer getHeight();

	Integer getSize();

	Float getDuration();

	Date getCreateTime();

	Date getShootTime();

	String getActivityId();

	Optional<Park> getPark();

	Optional<Game> getGame();

	Optional<Seat> getSeat();

	Optional<Activity> getActivity();
}
