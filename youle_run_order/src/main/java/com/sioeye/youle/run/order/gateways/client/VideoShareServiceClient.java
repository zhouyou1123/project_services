package com.sioeye.youle.run.order.gateways.client;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.ClipGoods;
import com.sioeye.youle.run.order.gateways.dto.DtoResult;
import com.sioeye.youle.run.order.gateways.dto.UploadWeishiDto;
import com.sioeye.youle.run.order.gateways.request.ShareWeishiUploadRequest;
import com.sioeye.youle.run.order.interfaces.VideoShareService;
import com.sioeye.youle.run.order.util.ClientHttpHeader;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class VideoShareServiceClient implements VideoShareService {
	
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 上传微视
	 * 
	 * @param uploadApi
	 * @param params
	 * @return boolean
	 */
	@Override
	public boolean uploadWeishi(String uploadApi, String shareCheckApi, String orderId, ClipGoods clipGoods) {
		HttpHeaders headers;
		try {
			headers = ClientHttpHeader.createHeaders();
			ShareWeishiUploadRequest ShareWeishiUploadRequest = new ShareWeishiUploadRequest(shareCheckApi, orderId,
					clipGoods);
			HttpEntity<ShareWeishiUploadRequest> request = new HttpEntity<>(ShareWeishiUploadRequest, headers);
			URI url;
			url = new URI(uploadApi);
			ParameterizedTypeReference<DtoResult<UploadWeishiDto>> typeRef = new ParameterizedTypeReference<DtoResult<UploadWeishiDto>>() {
			};
			ResponseEntity<DtoResult<UploadWeishiDto>> jsonObjectResponseEntity = restTemplate.exchange(url, HttpMethod.POST,
					request, typeRef);
			DtoResult<UploadWeishiDto> result = jsonObjectResponseEntity.getBody();
			if (!result.getSuccess()) {
				log.error("{" + "\"url\":\"" + uploadApi + "\"error\":\"" + result.getMessage() + "\"" + "}");
				throw new CustomException(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getCode(), String
						.format(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getMessage(), result.getMessage()));
			}
		} catch (NoSuchAlgorithmException ex) {
			log.error("{" + "\"url\":\"" + uploadApi + "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getMessage(), ex.getMessage()));
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"" + uploadApi + "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_UPLOAD_WEISIN_FAILURE.getMessage(), ex.getMessage()));
		}
		return true;
	}
}
