package com.sioeye.youle.run.order.interfaces;

import java.util.List;
import java.util.Map;

public interface ObjectStorageService {

	/**
	 * 
	 * @param bucketName
	 * @param objectName
	 * @return String
	 */
	public String getDownloadUrl(String bucketName, String objectName);

	/**
	 * 获取永久地址前缀
	 * 
	 * @param parkId
	 * @return
	 */
	public String getFinalUrlPrefix(String parkId);



	/**
	 * 获取商品的永久地址
	 * 
	 * @param prefixUrl
	 * @param tempUrl
	 * @param resourceType
	 *            0:预览视频资源，1：预览缩略图资源，2：下载视频资源，3：预览图片资源，4：下载图片资源
	 * @return
	 */
	public String getGoodsResourceFinalUrl(String prefixUrl, String tempUrl, int resourceType);

	/**
	 * 获取签名地址
	 *
	 * @param bucketName
	 * @param objectName
	 * @return
	 */
	public String getSignUrl(String bucketName, String objectName);

	/**
	 * 获取资源签名地址
	 * 
	 * @param resourceFilingStatue
	 *            资源状态
	 * @param url
	 *            资源url地址
	 * @return
	 */
	public String getSignUrl(int resourceFilingStatue, String url);

	/**
	 * 复制对象
	 * 
	 * @param fromBucketName
	 * @param objectKey
	 * @param toBucket
	 */
	public void copyObject(String fromBucketName, String objectKey, String toBucket);

	/**
	 * 拷贝资源到永久桶
	 * 
	 * @param orderId
	 * @param tasks
	 */
	public void copyToFinalBucket(String orderId, Map<String, String> tasks);

	/**
	 * 拷贝资源到永久桶
	 * 
	 * @param orderId
	 * @param tasks
	 */
	public void copyToFinalBucket(String orderId, List<Map<String, Object>> tasks);

	/**
	 * 将相对地址转换为绝对地址
	 * 
	 * @param relativeUrl
	 * @return
	 */
	public String convertAbsoluteUrl(String relativeUrl);

	/**
	 * S3地址转换为CDN地址
	 * 
	 * @param url
	 * @return
	 */
	public String convertCDNUrl(int resourceFilingStatue, String url);
}
