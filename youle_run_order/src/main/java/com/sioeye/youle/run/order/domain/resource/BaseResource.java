package com.sioeye.youle.run.order.domain.resource;

import com.sioeye.youle.run.order.domain.goods.Activity;
import com.sioeye.youle.run.order.domain.goods.Game;
import com.sioeye.youle.run.order.domain.goods.Park;
import com.sioeye.youle.run.order.domain.goods.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Data
@AllArgsConstructor
public class BaseResource implements Resource {
    private String resourceId;
    private String resourceName;
    private ResourceCategory resourceCategory;
    private Integer resourceType;
    private String parkId;
    private String parkName;
    private String gameId;
    private String gameName;
    private String seatId;
    private String seatName;
    private String seatSequenceNo;
    private String thumbnailUrl;
    private String previewUrl;
    private String downloadUrl;
    private Integer width;
    private Integer height;
    private Integer size;
    private Float duration;
    private Date createDate;
    private String activityId;
    private Date shootTime;

    public BaseResource(){}

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    @Override
    public Integer getResourceType() {
        return resourceType;
    }

    @Override
    public String getParkId() {
        return parkId;
    }

    @Override
    public String getParkName() {
        return parkName;
    }

    @Override
    public String getGameId() {
        return gameId;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    @Override
    public String getSeatId() {
        return seatId;
    }

    @Override
    public String getSeatName() {
        return seatName;
    }

    @Override
    public String getSeatSequenceNo() {
        return seatSequenceNo;
    }

    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public String getPreviewUrl() {
        return previewUrl;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public Float getDuration() {
        return duration;
    }

    @Override
    public Date getCreateTime() {
        return createDate;
    }

    @Override
    public String getActivityId() {
        return activityId;
    }

    @Override
    public Optional<Park> getPark() {
        if (StringUtils.hasText(this.parkId)){
            return Optional.of(new Park(this.parkId,this.parkName));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Game> getGame() {
        if (StringUtils.hasText(this.gameId)){
            return Optional.of(new Game(this.gameId,this.gameName));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Seat> getSeat() {
        if (StringUtils.hasText(this.seatId)){
            return Optional.of(new Seat(this.seatId,this.seatSequenceNo,this.seatName));
        }
        return Optional.empty();
    }
    @Override
    public Optional<Activity> getActivity() {
        if (StringUtils.hasText(this.activityId)){
            return Optional.of(new Activity(this.activityId));
        }
        return Optional.empty();
    }
}
