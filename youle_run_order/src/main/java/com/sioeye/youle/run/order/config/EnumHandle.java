package com.sioeye.youle.run.order.config;

/**
 * author zhouyou 
 * ckt email:jinx.zhou@ck-telecom.com
 * 2017年5月26日
 * EnumHandle.java 
 * description 错误枚举类
 */
public enum EnumHandle {

	INTERNAL_ERROR("110500","Internal Error . "),
	INSERT_ORDER_FAILED("110501","insert into order is failure . "),
	TRADE_TYPE_ERROR("110502","trade type is error . "),
	CALL_PAYMENT_FAILED("110503","call payment sever unified order is failed . "),
	PARAMS_INCORRECT("110505","params is incorrect . "),
	NOT_FOUND_ORDER("110506","order is not exist ."),
	CALL_VIDEOBOUGHT_FAILED("110507","call videobought server is failed . "),
	UPDATE_ORDER_FAILED("110508","update order is failed . "),
	PRICE_INCORRECT("110509","price is incorrect . "),
	SEND_VIDEOBOUGHT_QUEUE_FAILED("110510","send videobought queue is failed . "),
	VIDEO_EXISTED("110511","video is existed . "),
	ORDER_PAID("110512","order paid . "),
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
	CALL_MANAGEMENT_SEAT("110550","call core server is failed . "),
	CALL_CORE_FAILED("110551", "call core server is failed."),
	VIDEOCLIPS_NOT_EXIST("110552", "videoclips is not exist."),
	PLACE_ORDER_PHOTO_PARAM_INCORRECT("110553","place order params photoId is incorrect . "),
	PHOTO_PRICE_ERROR("110554","photo of park price is error . "),
	CALL_PHOTOBOUGHT_FAILED("110555", "call photobought server is failed . "),
	ORDER_TYPE_ERROR("110556","order type is error . "),
	PLACE_ORDER_GAMEID_PARAM_INCORRECT("110557","place order params gameId is incorrect . "),
	PLACE_ORDER_SEATID_PARAM_INCORRECT("110558","place order params seatId is incorrect . "),
	CALL_MANAGEMENT_PHOTO("110559","call core server is failed . "),
	HIGHLIGHTBOUGHT_NOT_EXIST("110560","highlight is not exist . "),
	CLIP_HAS_EXPIRED("110561","clip has expired . "),
	PHOTO_NOT_EXIST("110562","photo is not exist . "),
	PHOTO_HAS_EXPIRED("110563","photo has expired . "),
	COUPON_PRICE_VALIDATE_ERROR("110564","The coupon price and the parkPrice's price is not equal"),
	CANBUYGOODSTYPE_VALIDATE_ERROR("110565","ParkPrice's canBuyGoodsType does not contain the goodsType from the front page."),
	TIMESTAMP_CONVERSION_TIME_ERROR("110566","Timestamp conversion time error,maybe start time or end time!"),
	GET_PARK_PRICE_ERROR("110567","Getting a park price is failed"),
	VALIDATE_COUPON_FAILED("110568","Validate coupon is failed!"),
	ORDER_FAILED("110569","Order failed"),
	VALIDATE_SHARE_ACTIVITY_FAILED("110570","Validate share activity is failed!"),
	NOT_SUPPORT_SHARE("110571","Don't support share active!"),
	DISCOUNT_TYPE_INCORRECT("110572","The parameter discountType cannot be empty and must be an integer!"),
	VALIDATE_PRICE_FAILED("110573","Validate price is failed!"),
	VALIDATE_PARKSHAREID_UUID("110574","The parameter parkShareId cannot be empty and must be an uuid!"),
	SHARE_ACTIVITY_VALIDATE_FAILED("110575","Validate price of share activity is failed!"),
	NOT_SUPPORT_PARKDISCOUNT("110576","Don't support park discount active(%s)!"),
	PARAMS_NOT_MATCH("110577","params is not match"),
	VALIDATE_ORIGINAL_AMOUNT("110578","The original price of the front-end transmission is not equal to the original price of the sioeye youle Management platform setting!"),
	SHARE_PRICE_NOT_SET("110579","Share acrivity price isn't set in sioeye youle management platform!"),
	SHARE_ACTIVITY_IS_NOT_ENABLE("110580","Sharing activity is not enabled"),
	TYPE_ERROR("110581","type is error . "),
	PROMOTION_PRICE_NOT_EXIST("110582","promotion price isn't exist"),
	PROMOTION_PRICE_MISMATCH("110583","The current promotion price is inconsistent with the price of amusement park configurations"),
	CLIENT_TIME_NOT_IN_PROMOTION_PERIOD("110584","Client time is not within the validity period of the amusement park offer"),
	CAN_NOT_USE_PROMOTION("110585","Can't use the promotion,Maybe the verification of the quantity of the full price and the preferential price fails, maybe the promotion is not enabled, maybe the time is not within the validity period of the activity."),
	PRINT_PHOTO_PRICE_ERROR("110586","print photo price is error . "),
	INSERT_PHOTO_PRINT_ERROR("110587","insert photo print is error . "),
	CALL_PHOTOPRINT_FAILED("110588","call photoprint server is failed . "),
	CALL_USER_FAILED("110589","call user server is failed . "),
	SEND_USER_PAID_MESSAGE_FAILURE("110590","send user paid message is failed  . "),
	SHARE_ACTIVITY_IS_NOT_VALID("110591","Sharing activity is not valid"),
	PLACE_ORDER_SCENEID_PARAM_INCORRECT("110592","place order params sceneId is incorrect . "),
	PLACE_ORDER_DEVICERECORDID_PARAM_INCORRECT("110593","place order params deviceRecordId is incorrect . "),
	PAGENO_CAN_NOT_EMPTY("110594","pageNo can't empty"),
	PAGESIZE_CAN_NOT_EMPTY("110595","pageSize can't empty"),
	GOODSID_NOT_EMPTY("110596","goodsId can't empty"),
	GOODSTYPE_NOT_EMPTY("110597","goodsType or resourceTpe or resourceCategory can't empty"),
	COUPON_PARAM_ERROR("110598","the clipId of coupon tradeType can't empty"),
	GOODSLIST_PARAM_ERROR("110599","the goodsList can't empty");

	private String code;
	private String message;

	EnumHandle(String code, String message) {
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
