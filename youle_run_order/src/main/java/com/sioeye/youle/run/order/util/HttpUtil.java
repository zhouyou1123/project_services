package com.sioeye.youle.run.order.util;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月31日 HttpUtil.java
 * description 发送http请求类
 */
public class HttpUtil {

	private static final Log logger = LogFactory.getLog(HttpUtil.class);
	/**
	 * 创建header
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 *             Map<String,String>
	 */
	public static Map<String, String> createHeader(String appId, String secretKey) throws NoSuchAlgorithmException {
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("x_youle_flag", ConstUtil.X_YOULE_FLAG);
		headerMap.put("x_youle_parkid", ConstUtil.X_YOULE_PARKID);
		headerMap.put("x_youle_type", ConstUtil.X_YOULE_TYPE);
		headerMap.put("x_youle_appid", appId);
		long timeStamp = new Date().getTime();
		headerMap.put("x_youle_appsignkey", Util.HEXAndMd5(secretKey + timeStamp) + "," + timeStamp);
		return headerMap;
	}

	/**
	 * 创建header
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 *             Map<String,String>
	 */
	public static Map<String, String> createBaseHeader(String appId, String secretKey) throws NoSuchAlgorithmException {
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Type", "application/json");
		headerMap.put("x_youle_type", "1");
		headerMap.put("x_youle_appid", appId);
		long timeStamp = new Date().getTime();
		headerMap.put("x_youle_appsignkey", Util.HEXAndMd5(secretKey + timeStamp) + "," + timeStamp);
		return headerMap;
	}

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
			Map<String, String> requestHeaders) {
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
					// 设置header
					Set<String> headerKeys = requestHeaders.keySet();
					for (String headerKey : headerKeys) {
						conn.setRequestProperty(headerKey, requestHeaders.get(headerKey));
					}
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
					// 设置header
					Set<String> headerKeys = requestHeaders.keySet();
					for (String headerKey : headerKeys) {
						conn.setRequestProperty(headerKey, requestHeaders.get(headerKey));
					}
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
	 * 		public static Token getToken(String appid, String appsecret) {
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
	
	
	/**
	 * 
	 * @Description:发送HTTPPost请求,调用不同微服务接口
	 * @Author GuoGongwei
	 * @Time: 2019年6月13日上午11:32:18
	 * @param url
	 * @param json
	 * @return
	 */
	public static JSONObject doPost(String url, JSONObject json) {
		// 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse res = null;
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			// 发送json数据需要设置contentType
			s.setContentType("application/json");
			post.setEntity(s);
			res = httpClient.execute(post);
			if (res != null && res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(entity);
				// 返回json格式响应
				response = JSONObject.parseObject(result);
				return response;
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.printFailedLogToJson(logger, "send HTTPPost happended UnsupportedEncodingException", e);
		} catch (ClientProtocolException e) {
			LogUtil.printFailedLogToJson(logger, "send HTTPPost happended ClientProtocolException", e);
		} catch (IOException e) {
			LogUtil.printFailedLogToJson(logger, "send HTTPPost happended IOException", e);
		} finally {
			try {
				if (res != null) {
					res.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}