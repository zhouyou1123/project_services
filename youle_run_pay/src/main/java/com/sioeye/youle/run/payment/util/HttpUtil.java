package com.sioeye.youle.run.payment.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.ssl.SSLContexts;

import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.wxpay.MyX509TrustManager;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月31日 HttpUtil.java
 * description 发送http请求类
 */
public class HttpUtil {

    /**
     * 发送请求
     * 
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @param requestProperty
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr,
            String requestProperty) {
        String[] urls = requestUrl.split("\\://");
        if (urls != null && urls.length == 2) {
            try {
                if ("https".equals(urls[0])) {
                    // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                    TrustManager[] tm = { new MyX509TrustManager() };
                    SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                    sslContext.init(null, tm, new java.security.SecureRandom());
                    // 从上述SSLContext对象中得到SSLSocketFactory对象
                    SSLSocketFactory ssf = sslContext.getSocketFactory();
                    URL url = new URL(requestUrl);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setSSLSocketFactory(ssf);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    // 设置请求方式（GET/POST）
                    conn.setRequestMethod(requestMethod);
                    conn.setRequestProperty("content-type", requestProperty);
                    // 当outputStr不为null时向输出流写数据
                    if (null != outputStr) {
                        OutputStream outputStream = conn.getOutputStream();
                        // 注意编码格式
                        outputStream.write(outputStr.getBytes("UTF-8"));
                        outputStream.close();
                    }
                    // 从输入流读取返回内容
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    // 释放资源
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    inputStream = null;
                    conn.disconnect();
                    return buffer.toString();
                } else if ("http".equals(urls[0])) {
                    URL console = new URL(requestUrl);
                    HttpURLConnection conn = (HttpURLConnection) console.openConnection();
                    // 设置请求方式（GET/POST）
                    conn.setRequestMethod(requestMethod);
                    conn.setRequestProperty("Content-Type", requestProperty);
                    conn.setDoOutput(true);
                    // 当outputStr不为null时向输出流写数据
                    if (outputStr != null) {
                        BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());
                        hurlBufOus.write(outputStr.getBytes());
                        hurlBufOus.flush();
                        hurlBufOus.close();
                    }
                    // 从输入流读取返回内容
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    // 释放资源
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    inputStream = null;
                    conn.disconnect();
                    return buffer.toString();
                } else {
                    throw new CustomException(EnumHandle.HTTP_URL_IS_INCORRECT);
                }
            } catch (ConnectException ce) {
                ce.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new CustomException(EnumHandle.HTTP_URL_IS_INCORRECT);
        }
        return null;
    }

    public static String httpsWXTransfersRequest(String requestUrl, String p12Url, String outputStr, String mchId,
            String requestProperty) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, CertificateException, IOException {

        // 获取apiclient_cert.p12证书文件
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(p12Url));
        keyStore.load(instream, mchId.toCharArray());//
        instream.close();
        // 创建连接
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(ssf);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type", requestProperty);
        // 当outputStr不为null时向输出流写数据
        if (null != outputStr) {
            OutputStream outputStream = conn.getOutputStream();
            // 注意编码格式
            outputStream.write(outputStr.getBytes("UTF-8"));
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        return buffer.toString();
    }

    /**
     * 获取接口访问凭证
     * 
     * @param appid
     *            凭证
     * @param appsecret
     *            密钥
     * @return
     * 
     *         public static Token getToken(String appid, String appsecret) {
     *         Token token = null; String requestUrl =
     *         ConfigUtil.TOKEN_URL.replace("APPID", appid).replace("APPSECRET",
     *         appsecret); // 发起GET请求获取凭证 JSONObject jsonObject =
     *         JSONObject.fromObject(httpsRequest(requestUrl, "GET", null));
     * 
     *         if (null != jsonObject) { try { token = new Token();
     *         token.setAccessToken(jsonObject.getString("access_token"));
     *         token.setExpiresIn(jsonObject.getInt("expires_in")); } catch
     *         (JSONException e) { token = null; // 获取token失败
     *         log.error("获取token失败 errcode:{} errmsg:{}",
     *         jsonObject.getInt("errcode"), jsonObject.getString("errmsg")); }
     *         } return token; }
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}