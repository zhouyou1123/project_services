package com.sioeye.youle.run.order.domain;

public enum DomainErrorCodeEnum {

    INTERNAL_ERROR("110500","Internal Error . "),
    INSERT_ORDER_FAILED("110501","insert into order is failure . "),
    TRADE_TYPE_ERROR("110502","trade type is error . "),
    CALL_PAYMENT_FAILED("110503","call payment sever unified order is failed . "),
    PARAMS_INCORRECT("110505","params is incorrect . "),
    NOT_FOUND_ORDER("110506","order is not exist ."),
    CALL_VIDEOBOUGHT_FAILED("110507","call videobought server is failed . "),
    SAVE_ORDER_FAILED("110508","save order is failed . "),
    PRICE_INCORRECT("110509","price is incorrect . "),
    SEND_VIDEOBOUGHT_QUEUE_FAILED("110510","send videobought queue is failed . "),
    VIDEO_EXISTED("110511","video is existed . "),
    REMOVE_ORDERS_FAILED("110590","remove orders is failed . "),
    PLACE_ORDER_PARKID_PARAM_INCORRECT("110513","place order params amusementParkId is incorrect . "),
    PLACE_ORDER_USERID_PARAM_INCORRECT("110514","place order params session userId is incorrect . "),
    PLACE_ORDER_ACTIVITYID_PARAM_INCORRECT("110515","place order params activityId is incorrect . "),
    PLACE_ORDER_CLIP_PARAM_INCORRECT("110516","place order params clip is incorrect . "),
    PLACE_ORDER_CLIPCOUNT_PARAM_INCORRECT("110517","place order params clipCount is incorrect . "),
    PLACE_ORDER_ORIGINALAMOUNT_PARAM_INCORRECT("110518","place order params originalAmount is incorrect . "),
    PLACE_ORDER_ACTUALAMOUNT_PARAM_INCORRECT("110519","place order params actualAmount is incorrect . "),
    PLACE_ORDER_PAYWAY_PARAM_INCORRECT("110520","place order params payWay is incorrect . "),
    PLACE_ORDER_OPENID_PARAM_INCORRECT("110521","place order params openId is incorrect . "),
    VIDEO_PRICE_ERROR("110522","video of park price is error . "),
    USER_CREATING_ORDER("110523","user is creating an order . "),
    HTTP_URL_IS_INCORRECT("110524","http url is incorrect . "),
    PARK_PROMOTION_TIME_ERROR("110525","amusement park promotion time is error . "),
    USERID_PARAM_INCORRECT("110526","param userId is incorrect . "),
    PARKID_PARAM_INCORRECT("110527","param parkId is incorrect . "),
    DATE_PARAM_INCORRECT("110528","param date is incorrect . "),
    USER_MERGEING_HIGHLIGHT("110529","user is merging highlight . "),
    UPDATE_USER_MERGEVIDEO_STATUS_FAILURE("110530","update user mergevideo status is failure . "),
    PLACE_ORDER_HIGHLIGHTID_PARAM_INCORRECT("110531","place order params highlight is incorrect . "),
    HIGHLIGHT_PRICE_ERROR("110532","highlight of park price is error . "),
    CALL_HIGHLIGHTBOUGHT_FAILED("110533","call highlightbought server is failed . "),
    ORDER_UNPAY("110534","order is not pay . "),
    PAYMENT_EMPTY("110535","payment is empty . "),
    CALL_ORDERHIGHLIGHT_FAILED("110536","call orderhighlight server is failed . "),
    ORDER_HASNOT_PAYMENT("110537","order has not payment . "),
    SEND_LINE_ROT_VIDEO_FAILURE("110538","send line rot video is failure . "),
    PLACE_ORDER_CURRENCY_PARAM_INCORRECT("110539","place order params currency is incorrect . "),
    PLACE_ORDER_FORMID_PARAM_INCORRECT("110540","place order params formId is incorrect . "),
    PLACE_ORDER_VIDEONAME_PARAM_INCORRECT("110541","place order params videoName is incorrect . "),
    PLACE_ORDER_THUMBNAILURL_PARAM_INCORRECT("110542","place order params thumbnailUrl is incorrect . "),
    PLACE_ORDER_PAYPAL_SUCCESSURL_PARAM_INCORRECT("110543","place order params paypal success url is incorrect  . "),
    CALL_CLIPBOUGHT_FAILURE("110544","call clipbought is failure . "),
    SEND_WEIXIN_MESSAGE_FAILURE("110545","send weixin pay success message is failure . "),
    PLACE_ORDER_SHORTVIDEO_PARAM_INCORRECT("110546","place order params shortVideo is incorrect . "),
    CALL_CORE_CHECK_STATE_FAILURE("110547","call core server check weishi file state is failure . "),
    WEISHI_SHARE_QUEUE_DATA_ERROR("110548","weishi share queue result data is error . "),
    VIDEOCLIPS_NOT_FOUND_SEAT("110549","videoclips is not found seat information ."),
    CALL_MANAGEMENT_SEAT("110550","call core server is failed .(%s) "),
    CALL_CORE_FAILED("110551", "call core server is failed.(%s)"),
    VIDEOCLIPS_NOT_EXIST("110552", "resource is not exist.(%s)"),
    PLACE_ORDER_PHOTO_PARAM_INCORRECT("110553","place order params photoId is incorrect . "),
    PHOTO_PRICE_ERROR("110554","photo of park price is error . "),
    CALL_PHOTOBOUGHT_FAILED("110555", "call photobought server is failed . "),
    ORDER_TYPE_ERROR("110556","order type is error . "),
    PLACE_ORDER_GAMEID_PARAM_INCORRECT("110557","place order params gameId is incorrect . "),
    PLACE_ORDER_SEATID_PARAM_INCORRECT("110558","place order params seatId is incorrect . "),
    CALL_MANAGEMENT_PHOTO("110559","call core server is failed . "),
    DUPLICATE_BUY ("110512","duplicate buy goods(%s)."),
    COUPON_NOTIN_PERIOD ("110561","the place order date be not in coupon period."),
    COUPON_CALC_PERIOD_ERROR ("110562","calc place order date local date be error."),
    BUYER_NOTBUY_COUPON ("110563","user be not buy coupon."),
    INVALID_GOODSTYPE ("110564","invalid goods type."),
    SEND_ORDER_PAYMENT_QUEUE_ERROR ("110565","it is error for send the zero price order."),
    LOCK_REDIS_ERROR ("110566","lock redis key is error."),
    UNLOCK_REDIS_ERROR ("110567","unlock redis key is error. %s"),
    POST_PARK_PRICE_ERROR ("110568","get park price error by admin-park service."),
    POST_PAY_STATUS_ERROR ("110569","get pay status error by payment service."),
    POST_PARK_COUPON_ERROR ("110570","get park coupon info error by admin-park service."),
    INVALID_GOODS ("110571","invalid goods or goods sold out (%s)."),
    INVALID_ORDER_ITEM ("110572","order item is invalid ."),
    POST_PARK_SHARE_ACTIVITY_ERROR ("110573","get park share activity error by admin-park service."),
    POST_PARK_SHARE_ACTIVITY_FAILED ("110573","park share activity is null.(%s)."),
    COUPON_BUY_GOODS_TYPE_FAILED ("110574","The range of goods type purchased with a set of coupon cannot be empty.(%s)."),
    GOODS_OVERDUE ("110575","Goods is overdue(%s)."),
    PROMOTION_PERIOD_TYPE_ERROR ("110576","the period type of promotion must less then 1."),
    PARK_TIMEZONE_ERROR ("110577","the timeZone of park is error.(%s)"),
    PARK_NOT_PROMOTION_ERROR("110578","the promotion of park is not exist."),
    CLIP_HAS_EXPIRED("110579","clip has expired . "),
    PHOTO_NOT_EXIST("110580","photo is not exist . "),
    PHOTO_HAS_EXPIRED("110581","photo has expired . "),
    GOODS_NOT_FOUND_SEAT("110582","goods is not found seat information ."),
    CALL_PARK_SEAT_FAILED("110583","call core server is failed .(%s)"),
    ORDER_ID_NOT_EMPTY("110584","order id is not empty. (%s)"),
    GOODS_SOLD_OUT("110585","goods is sold out.(%s)"),
    GOODS_PARKID_NOT_EQUAL_REQUEST_PARKID_SOLD_OUT("110586","goods parkid(%s) is not equal request parkid(%s)."),
    GOODS_ID_NOT_EMPTY("110587","goods id is not empty."),
    GOODS_COUNT_MAX_ERROR("110588","the count of goodsType(%s) is not >=2."),
    GOODS_PARAM_MISS("110589","it is missing the type(%s) of goods."),
    PLACE_ORDER_DEVICERECORDID_ERROR("110590","place order params deviceRecordId is incorrect .."),
    PLACE_ORDER_SCENEID_ERROR("110591","place order params sceneId is incorrect .."),
    CALL_CORE_UPLOAD_WEISIN_FAILURE("110592","call core server upload weishi is failure .(%s) "),
    PAID_COUPON_ERROR("110593","buyer paid coupon is error ."),
    DUPLICATE_COUPON_ERROR("110593","coupon(4) is bought,not buy gamecoupon(%s).");
    private String code;
    private String message;

    DomainErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
