package com.sioeye.youle.run.order.gateways.repository.dao;

import com.sioeye.youle.run.order.gateways.repository.dataobject.UserCouponDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * UserCouponDao继承基类
 */
@Repository
public interface UserCouponDao extends MyBatisBaseDao<UserCouponDo, String> {
    public UserCouponDo getUserCoupon(@Param("userid") String userid, @Param("amusementparkid") String amusementparkid);
    public UserCouponDo getUserGameCoupon(@Param("userid") String userid, @Param("couponid") String couponid);
    public UserCouponDo getUserGameCouponByGameId(@Param("userid") String userid, @Param("amusementparkid") String amusementparkid,@Param("gameid") String gameid);
}