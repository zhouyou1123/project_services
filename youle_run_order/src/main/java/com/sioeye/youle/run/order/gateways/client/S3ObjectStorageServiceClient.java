package com.sioeye.youle.run.order.gateways.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sioeye.youle.run.order.config.AwsCloudFrontProperties;
import com.sioeye.youle.run.order.config.AwsS3Properties;
import com.sioeye.youle.run.order.gateways.mq.OrderFillingProducer;
import com.sioeye.youle.run.order.gateways.mq.dto.OrderFilingMessage;
import com.sioeye.youle.run.order.interfaces.ObjectStorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class S3ObjectStorageServiceClient implements ObjectStorageService {

	private AmazonS3 s3;
	private OrderFillingProducer orderFillingProducer;
	private AwsS3Properties awsS3Properties;
	@Value("${order.filling.copy-response-queue}")
	private String callBackQueue;
	private AwsCloudFrontProperties cloudFrontProperties;
	@Value("${env}")
	private String env;

	public S3ObjectStorageServiceClient(OrderFillingProducer orderFillingProducer, AwsS3Properties awsS3Properties,
			AwsCloudFrontProperties cloudFrontProperties) {

		this.orderFillingProducer = orderFillingProducer;
		this.awsS3Properties = awsS3Properties;
		this.cloudFrontProperties = cloudFrontProperties;
	}

	@PostConstruct
	private void initialize() {

		AWSCredentials credentials = new BasicAWSCredentials(awsS3Properties.getKey().getAccess(),
				awsS3Properties.getKey().getSecret());
		s3 = new AmazonS3Client(credentials);
		Region region = Region.getRegion(Regions.fromName(awsS3Properties.getRegion()));
		s3.setRegion(region);
	}

	@Override
	public String getDownloadUrl(String bucketName, String objectName) {
		// 初始化连接
		this.initialize();
		if (bucketName.isEmpty() || objectName.isEmpty()) {
			return null;
		}
		// 获取当前时间
		Date expiration = new Date();
		long milliSeconds = expiration.getTime();
		// 增加url有效期
		milliSeconds += awsS3Properties.getUrlExpiration();
		expiration.setTime(milliSeconds);
		// 创建请求
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
		// 设置url访问权限
		request.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
		// 设置请求方法
		request.setMethod(HttpMethod.GET);
		// 设置有效期
		request.setExpiration(expiration);
		URL url = s3.generatePresignedUrl(request);
		return url.toString();
	}

	@Override
	public String getFinalUrlPrefix(String parkId) {
		// 拼接永久下载地址
		return String.format("%s/%s/%s", awsS3Properties.getAeon().getAddr(), env, parkId);
	}

	@Override
	public String getGoodsResourceFinalUrl(String prefixUrl, String tempUrl, int resourceType) {
		if (!StringUtils.hasText(prefixUrl) || !StringUtils.hasText(tempUrl)) {
			return null;
		}
		// 0:预览视频资源，1：预览缩略图资源，2：下载视频资源，3：预览图片资源，4：下载图片资源
		if (resourceType == 0) {
			return String.format("%s%s%s", prefixUrl, "/videos/preview", (tempUrl.substring(tempUrl.lastIndexOf("/"))));
		} else if (resourceType == 1) {
			return String.format("%s%s%s", prefixUrl, "/images", (tempUrl.substring(tempUrl.lastIndexOf("/"))));
		} else if (resourceType == 2) {
			if (tempUrl.indexOf("?") >= 0) {
				return String.format("%s%s%s", prefixUrl, "/videos",
						(tempUrl.substring(tempUrl.lastIndexOf("/"), tempUrl.indexOf("?"))));
			}
			return String.format("%s%s%s", prefixUrl, "/videos", (tempUrl.substring(tempUrl.lastIndexOf("/"))));
		} else {
			return String.format("%s%s%s", prefixUrl, "/images", (tempUrl.substring(tempUrl.lastIndexOf("/"))));
		}
	}

	@Override
	public String getSignUrl(String bucketName, String objectName) {

		if (!StringUtils.hasText(bucketName)) {
			log.info("{" + "\"aws-function\":\"getSignUrl\"," + "\"s3-message\":\"bucket name is empty.\"" + "}");
			return null;
		}
		if (!StringUtils.hasText(objectName)) {
			log.info("{" + "\"aws-function\":\"getSignUrl\"," + "\"s3-message\":\"resource name is empty.\"" + "}");
			return null;
		}
		// 获取当前时间
		Date expiration = new Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += awsS3Properties.getUrlExpiration();
		// 设置过期时间
		expiration.setTime(milliSeconds);
		try {
			// 创建请求
			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
			// 设置url访问权限
			request.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
			// 设置请求方法
			request.setMethod(HttpMethod.GET);
			// 设置有效期
			request.setExpiration(expiration);
			URL url = s3.generatePresignedUrl(request);
			return url.toString();
		} catch (Exception ex) {
			log.info("{" + "\"aws-function\":\"getSignUrl\"," + "\"bucket\":\"" + bucketName + "\","
					+ "\"objectName\":\"" + objectName + "\"," + "\"error-message\":\"" + ex.getMessage() + "\"" + "}");
		}
		return null;

	}

	@Override
	public String getSignUrl(int resourceFilingStatue, String url) {
		if (!StringUtils.hasText(url)) {
			return null;
		}
		String urlTemp = new String(url);
		if (urlTemp.contains("http://") || urlTemp.contains("https://")) {
			urlTemp = urlTemp.replace("http://", "");
			urlTemp = urlTemp.replace("https://", "");
		}
		if (resourceFilingStatue == 1) {
			if (urlTemp.indexOf("?") >= 0) {
				return this.signUrlConvertCDNUrl(resourceFilingStatue, getSignUrl(awsS3Properties.getTmp().getBucket(),
						urlTemp.substring(urlTemp.indexOf("/") + 1, urlTemp.indexOf("?"))));
			} else {
				return this.signUrlConvertCDNUrl(resourceFilingStatue,
						getSignUrl(awsS3Properties.getTmp().getBucket(), urlTemp.substring(urlTemp.indexOf("/") + 1)));
			}
		} else if (resourceFilingStatue == 2) {
			return this.signUrlConvertCDNUrl(resourceFilingStatue,
					getSignUrl(awsS3Properties.getAeon().getBucket(), urlTemp.substring(urlTemp.indexOf("/") + 1)));
		}
		return null;
	}

	@Override
	public void copyObject(String fromBucketName, String objectKey, String toBucket) {

	}

	@Override
	public void copyToFinalBucket(String orderId, Map<String, String> tasks) {

		List<Map<String, Object>> taskList = new ArrayList<>(tasks.size());
		for (Map.Entry<String, String> entry : tasks.entrySet()) {
			Map<String, Object> map = new HashMap<>(3);
			map.put("src", entry.getKey());
			map.put("dst", entry.getValue());
			if (entry.getKey().endsWith(".jpg") || entry.getKey().endsWith(".bmp") || entry.getKey().endsWith(".png")
					|| entry.getKey().endsWith(".gif")) {
				map.put("private", false);
			}
			taskList.add(map);
		}
		// orderFilingTopic.output().send(MessageBuilder.withPayload(new
		// OrderFilingMessage(orderId,callBackQueue,taskList)).build());
	}

	@Override
	public void copyToFinalBucket(String orderId, List<Map<String, Object>> tasks) {

		orderFillingProducer.sendOrderFillingMessage(
				JSONObject.toJSONString(new OrderFilingMessage(orderId, callBackQueue, tasks)));
	}

	@Override
	public String convertAbsoluteUrl(String relativeUrl) {
		return String.format("%s/%s", awsS3Properties.getTmp().getAddr(), relativeUrl);
	}

	private String signUrlConvertCDNUrl(int resourceFilingStatue, String urlAddress) {
		if (!StringUtils.hasText(urlAddress)) {
			return null;
		}
		if (urlAddress.contains("http://") || urlAddress.contains("https://")) {
			urlAddress = urlAddress.replace("http://", "");
			urlAddress = urlAddress.replace("https://", "");
		}
		urlAddress = urlAddress.substring(urlAddress.indexOf("/") + 1, urlAddress.length());
		if (resourceFilingStatue == 2) {
			return cloudFrontProperties.getAeon() + "/" + urlAddress;
		} else {
			return cloudFrontProperties.getTmp() + "/" + urlAddress;
		}
	}

	@Override
	public String convertCDNUrl(int resourceFilingStatue, String urlAddress) {
		if (!StringUtils.hasText(urlAddress)) {
			return null;
		}
		try {
			URL url = new URL(urlAddress);
			if (resourceFilingStatue == 2) {
				return cloudFrontProperties.getAeon() + url.getPath();
			} else {
				return cloudFrontProperties.getTmp() + url.getPath();
			}
		} catch (MalformedURLException e) {
			log.error("\"s3Url\":\"" + urlAddress + "\"," + "\"error\":\"" + e.getMessage() + "\"");
		}
		return null;
	}

	// public static void main(String[] args){
	// //https://pre-yltmp.sioeye.com/result/6be7cc4c58c445a3818afff378f400be/b6f000251d044adc8297ba3488ceaec4/8964b18fc195443cb8f2bb7cc0ff6187/images/1875a0fe75694b8a8ce6ad4da04444ca_preview.jpg
	// String urlAddress1
	// ="https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/6be7cc4c58c445a3818afff378f400be/b6f000251d044adc8297ba3488ceaec4/8964b18fc195443cb8f2bb7cc0ff6187/videos/8d34ada13bb44bfcaa27377b729c7f90.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAO7QXSMTQKKQGQWRQ%2F20191011%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20191011T050145Z&X-Amz-Expires=600&X-Amz-Signature=6e98f50d016fca3037d96432da6f0fd484c6f3482e77a3931ddb5b635af2c28e&X-Amz-SignedHeaders=host";
	// String urlAddress
	// ="https://sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn/result/6be7cc4c58c445a3818afff378f400be/b6f000251d044adc8297ba3488ceaec4/8964b18fc195443cb8f2bb7cc0ff6187/images/1875a0fe75694b8a8ce6ad4da04444ca_preview.jpg";
	// try {
	// URL url = new URL(urlAddress1);
	// System.out.println(url.getHost());
	// System.out.println(url.getPath());
	// System.out.println(url.getQuery());
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// }
}
