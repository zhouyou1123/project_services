package com.sioeye.youle.run.payment.wxpay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月31日
 * PayCommonUtil.java description 支付工具类
 */
public class PayCommonUtil {

	public static final String ALGORITHM = "SHA-256";

	/**
	 * @author zhouyou
	 * @date 2014-12-5下午2:29:34
	 * @Description：sign签名
	 * @param characterEncoding
	 *            编码格式
	 * @param parameters
	 *            请求参数
	 * @return
	 */
	public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters, String appKey) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<Object, Object>> es = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = es.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + appKey);
		return MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
	}

	/**
	 * 将请求参数转换为xml格式的string
	 * 
	 * @param parameters
	 * @return
	 */
	public static String getRequestXml(SortedMap<Object, Object> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set<Entry<Object, Object>> es = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = es.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)
					|| "nonce_str".equalsIgnoreCase(k)) {
				sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
			} else {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public static SortedMap<String, String> doXMLParse(String strxml) throws IOException, ParserConfigurationException, SAXException {
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
		if (null == strxml || "".equals(strxml)) {
			return null;
		}
		SortedMap<String, String> m = new TreeMap<String, String>();
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		// SAXBuilder builder = new SAXBuilder();
		// Document doc = safebuilder.build(in);
		// Element root = doc.getRootElement();
		// while (it.hasNext()) {
		// Element e = (Element) it.next();
		// String k = e.getName();
		// String v = "";
		// List<Element> children = e.getChildren();
		// if (children.isEmpty()) {
		// v = e.getTextNormalize();
		// } else {
		// v = PayCommonUtil.getChildrenText(children);
		// }
		// m.put(k, v);
		// }
		// // 关闭流
		// in.close();
		// return m;
		// List<Element> list = root.getChildren();
		// Iterator<Element> it = list.iterator();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
		// almost all XML entity attacks are prevented
		// Xerces 2 only -
		// http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		DocumentBuilder safebuilder = dbf.newDocumentBuilder();
		Document doc = safebuilder.parse(in);
		doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int idx = 0; idx < nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                m.put(element.getNodeName(), element.getTextContent());
            }
        }
		// 关闭流
		in.close();
		return m;
	}

	/**
	 * 返回给微信的参数
	 * 
	 * @param return_code
	 * @param return_msg
	 * @return
	 */
	public static String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}

	/**
	 * 得到签名
	 * 
	 * @return
	 */
	public static String getSioeyeSign(Long date, String ordersId, String appId, String nonceStr, String sioeyeKey) {
		// 将imei号装换为char 组
		char[] chars = nonceStr.toCharArray();
		// 偶数位字符串
		StringBuffer evens = new StringBuffer();
		// 奇数位字符串
		StringBuffer odds = new StringBuffer();
		// result original
		String original = "";
		if (date % 2 == 0) {
			// 偶数
			for (int i = 0; i < chars.length; i = i + 2) {
				evens.append(chars[i]);
			}
			original = ordersId + date + appId + evens.toString() + sioeyeKey;
		} else {
			// 奇数
			for (int i = 1; i < chars.length; i = i + 2) {
				odds.append(chars[i]);
			}
			original = ordersId + date + appId + odds.toString() + sioeyeKey;
		}
		// signvalue
		String sioeyeSign = "";
		// 调用加密算法
		sioeyeSign = PayCommonUtil.SHA256Encrypt(original);
		return sioeyeSign;
	}

	/**
	 * 加密算法
	 * 
	 * @param original
	 * @return
	 */
	public static String SHA256Encrypt(String original) {
		if (original != null && !"".equals(original)) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance(ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if (null != md) {
				byte[] origBytes = original.getBytes();
				md.update(origBytes);
				byte[] digestRes = md.digest();
				String digestStr = getDigestStr(digestRes);
				return digestStr;
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * SHA-256加密算法具体实现
	 * 
	 * @param origBytes
	 * @return
	 */
	private static String getDigestStr(byte[] origBytes) {
		String tempStr = null;
		StringBuilder stb = new StringBuilder();
		for (int i = 0; i < origBytes.length; i++) {
			// 这里按位与是为了把字节转整时候取其正确的整数，Java中一个int是4个字节
			// 如果origBytes[i]最高位为1，则转为int时，hint的前三个字节都被1填充了
			tempStr = Integer.toHexString(origBytes[i] & 0xff);
			if (tempStr.length() == 1) {
				stb.append("0");
			}
			stb.append(tempStr);
		}
		return stb.toString();
	}

	/**
	 * 判断微信返回的map结果是否正确
	 * 
	 * @param map
	 * @return
	 */
	public static boolean validityWeiXinReturnMap(Map<String, String> map) {
		boolean result = false;
		if (!(map != null && map.size() > 0)) {
			return result;
		}
		// 判断sign签名
		String sign = map.get("sign");
		if (!(sign != null && !"".equals(sign))) {
			result = false;
			return result;
		}
		// 判断nonceStr随机字符串
		String nonceStr = map.get("nonce_str");
		if (!(nonceStr != null && !"".equals(nonceStr))) {
			result = false;
			return result;
		}
		// 判断tradeType随机字符串
		String tradeType = map.get("trade_type");
		if (!(tradeType != null && !"".equals(tradeType))) {
			result = false;
			return result;
		}
		// 判断prepayId随机字符串
		String prepayId = map.get("prepay_id");
		if (!(prepayId != null && !"".equals(prepayId))) {
			result = false;
			return result;
		}
		result = true;
		return result;
	}

	public static String createSignStr(String characterEncoding, Map<String, String> parameters, String appKey) {
		Map<String, String> sortMap = new TreeMap<>();
		sortMap.putAll(parameters);
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = sortMap.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + appKey);
		String rt=MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return rt;
	}
}
