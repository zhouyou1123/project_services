package com.sioeye.youle.run.order.util;

import org.springframework.http.HttpHeaders;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientHttpHeader {

    public static HttpHeaders createHeaders() throws NoSuchAlgorithmException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x_youle_flag", "1");
        headers.set("x_youle_parkid", "sioeye");
        headers.set("x_youle_type", "1");
        headers.set("x_youle_appid", "548b7e55e809a8eeaec18ededb29b659s");
        long timeStamp = new Date().getTime();
        headers.set("x_youle_appsignkey", AppSignKey.HEXAndMd5("74bcc8e24f14137e16bfaead39aa3388" + timeStamp) + "," + timeStamp);
        return headers;
    }

    public static Map<String,String> createHeadersMap() throws NoSuchAlgorithmException {
        Map<String,String>  headers = new HashMap<String,String>();
        headers.put("Content-Type", "application/json");
        headers.put("x_youle_flag", "1");
        headers.put("x_youle_parkid", "sioeye");
        headers.put("x_youle_type", "1");
        headers.put("x_youle_appid", "548b7e55e809a8eeaec18ededb29b659s");
        long timeStamp = new Date().getTime();
        headers.put("x_youle_appsignkey", AppSignKey.HEXAndMd5("74bcc8e24f14137e16bfaead39aa3388" + timeStamp) + "," + timeStamp);
        return headers;
    }
}
