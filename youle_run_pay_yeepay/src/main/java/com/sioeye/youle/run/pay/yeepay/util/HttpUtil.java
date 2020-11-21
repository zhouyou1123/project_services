package com.sioeye.youle.run.pay.yeepay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author zhouyou
 * @cktEmail:jinx.zhou@ck-telecom.com
 * @date 2018年5月22日
 * @fieleName HttpUtil.java
 * @TODO基于 httpclient 4.5版本的 http工具类
 */
public class HttpUtil {

	private static final CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8";
	// 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(60000).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(1000);
		connManager.setDefaultMaxPerRoute(500);
		// 设置重试
		HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount > 2) {
					return false;
				}
				return true;
			}
		};
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(connManager)
				.setRetryHandler(handler).build();
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
		headerMap.put("x_youle_type", ConstUtil.X_YOULE_TYPE);
		headerMap.put("x_youle_appid", appId);
		long timeStamp = new Date().getTime();
		headerMap.put("x_youle_appsignkey", Util.HEXAndMd5(secretKey + timeStamp) + "," + timeStamp);
		headerMap.put("x_youle_parkid", ConstUtil.X_YOULE_PARKID);
		return headerMap;
	}

	/**
	 * 发送http请求
	 * 
	 * @param url
	 * @param params
	 * @param header
	 * @return
	 * @throws IOException
	 *             String
	 */
	public static String doPostJson(String url, Map<String, Object> params, Map<String, String> header)
			throws IOException {
		return doPostJson(url, params, header, CHARSET);
	}

	public static String doPostJson(String url, Map<String, Object> params, Map<String, String> header, String charset)
			throws IOException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		JSONObject paramsJson = new JSONObject();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				paramsJson.put(entry.getKey(), entry.getValue());
			}
		}
		HttpPost httpPost = new HttpPost(url);

		Set<String> set = header.keySet();
		for (String key : set) {
			httpPost.setHeader(key, getValueEncoded(header.get(key)));
		}

		if (paramsJson != null) {
			StringEntity stringEntity = new StringEntity(paramsJson.toJSONString(), CHARSET);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
		}
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (response != null)
				response.close();
		}
		return null;
	}

	private static String getValueEncoded(String value) throws UnsupportedEncodingException {
		if (value == null)
			return "null";
		String newValue = value.replace("\n", "");
		for (int i = 0, length = newValue.length(); i < length; i++) {
			char c = newValue.charAt(i);
			if (c <= '\u001f' || c >= '\u007f') {
				return URLEncoder.encode(newValue, "UTF-8");
			}
		}
		return newValue;
	}

	/**
	 * 这里创建了忽略整数验证的CloseableHttpClient对象
	 * 
	 * @return
	 */
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}