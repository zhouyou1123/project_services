package com.sioeye.youle.run.order.domain.timezone;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


/**
 * period 23日一天
 * >= getPeriodStartDate: 2019-07-23 00:00:00
 * < getPeriodEndDate: 2019-07-24 00:00:00
 *
 */
@Getter
public class ParkTimeZone {
    private ZoneId zoneId;
    private String name;
    public ParkTimeZone(String timeZone){
        try {
            name = timeZone;
            this.zoneId = ZoneId.of(timeZone);
        }catch (Exception ex){
            throw new CustomException(DomainErrorCodeEnum.PARK_TIMEZONE_ERROR.getCode(),String.format(DomainErrorCodeEnum.PARK_TIMEZONE_ERROR.getMessage(),ex.getMessage()));
        }
    }

    public Date getLocalDate(Date date){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }


    /**
     * 获取周期的本地开始日期
     * @param currentDate 当前（下单）日期
     * @param periodType 0今天，-1昨天到今天，-2前天到今天，依次类推
     * @return
     */
    public Date getPeriodStartDate(Date currentDate, int periodType){
        checkPeriodType(periodType);
        LocalDateTime currentLocalDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), zoneId);
        currentLocalDateTime = currentLocalDateTime.plusDays(periodType);
        LocalDateTime startLocalDateTime = LocalDateTime.of(currentLocalDateTime.getYear(),currentLocalDateTime.getMonth(),currentLocalDateTime.getDayOfMonth(),0,0,0,0);
        return Date.from(startLocalDateTime.atZone(zoneId).toInstant());
    }

    /**
     * 获取周期的本地结束日期
     * @param currentDate 当前（下单）日期
     * @param periodType 0今天，-1昨天到今天，-2前天到今天，依次类推
     * @return
     */
    public Date getPeriodEndDate(Date currentDate, int periodType){
        checkPeriodType(periodType);

        LocalDateTime currentLocalDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), zoneId);
        currentLocalDateTime = currentLocalDateTime.plusDays(1);
        LocalDateTime endLocalDateTime = LocalDateTime.of(currentLocalDateTime.getYear(),currentLocalDateTime.getMonth(),currentLocalDateTime.getDayOfMonth(),0,0,0,0);
        return Date.from(endLocalDateTime.atZone(zoneId).toInstant());
    }
    private void checkPeriodType(int periodType){
        if (periodType>0){
            throw new CustomException(DomainErrorCodeEnum.PROMOTION_PERIOD_TYPE_ERROR.getCode(), DomainErrorCodeEnum.PROMOTION_PERIOD_TYPE_ERROR.getMessage());
        }
    }
    public static ParkTimeZone buildDefault(){
        return new ParkTimeZone("Asia/Shanghai");
    }
}
