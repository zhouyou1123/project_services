package com.sioeye.youle.run.order.gateways.client;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.*;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.resource.BaseResource;
import com.sioeye.youle.run.order.gateways.dto.ClipDto;
import com.sioeye.youle.run.order.gateways.dto.DtoResult;
import com.sioeye.youle.run.order.gateways.dto.PhotoDto;
import com.sioeye.youle.run.order.gateways.dto.SeatDto;
import com.sioeye.youle.run.order.gateways.request.ClipDtoRequest;
import com.sioeye.youle.run.order.gateways.request.PhotoDtoRequest;
import com.sioeye.youle.run.order.gateways.request.ResourceDtoRequest;
import com.sioeye.youle.run.order.gateways.request.SeatDtoRequest;
import com.sioeye.youle.run.order.interfaces.ObjectStorageService;
import com.sioeye.youle.run.order.interfaces.RunCoreService;
import com.sioeye.youle.run.order.util.ClientHttpHeader;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.caucho.BurlapClientInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j
public class RunCoreServiceClient implements RunCoreService {

	@Value("${resource.url.seat-video:null}")
	private String clipServiceUrl;
	@Value("${resource.url.point-photo:null}")
	private String photoServiceUrl;
	@Value("${resource.url.seat-info:null}")
	private String seatServiceUrl;
	@Value("${resource.url.clipid}")
	private String clipIdServiceUrl;
	@Value("${resource.url.aggregation}")
	private String resourceAggregationUrl;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectStorageService objectStorageService;

	@Override
	public Goods getClip(String clipId) {
		try {
			HttpHeaders headers = ClientHttpHeader.createHeaders();
			ClipDtoRequest clipDtoRequest = new ClipDtoRequest(clipId);
			HttpEntity<ClipDtoRequest> request = new HttpEntity<>(clipDtoRequest, headers);
			URI url = new URI(clipServiceUrl);
			ParameterizedTypeReference<DtoResult<List<ClipDto>>> typeRef = new ParameterizedTypeReference<DtoResult<List<ClipDto>>>() {
			};
			ResponseEntity<DtoResult<List<ClipDto>>> jsonObjectResponseEntity = restTemplate.exchange(url,
					HttpMethod.POST, request, typeRef);
			DtoResult<List<ClipDto>> dtoResult = jsonObjectResponseEntity.getBody();
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null
					&& dtoResult.getValue().size() > 0) {
				ClipDto clipDto = dtoResult.getValue().get(0);
				if (!StringUtils.hasText(clipDto.getPreviewurl())) {
					throw new RuntimeException("the previewurl of clip is null.");
				}
				if (!StringUtils.hasText(clipDto.getThumbnailurl())) {
					throw new RuntimeException("the thumbnailurl of clip is null.");
				}
				if (!StringUtils.hasText(clipDto.getDownloadurl())) {
					throw new RuntimeException("the downloadurl of clip is null.");
				}
				Park park = createPark(clipDto.getParkid(), clipDto.getParkname());
				Game game = createGame(clipDto.getGameid(), clipDto.getGamename());
				Seat seat = createSeat(clipDto.getSeatid());
				Activity activity = new Activity(clipDto.getActivityid());
				Clip clip = new Clip(clipDto.getDownloadurl(), clipDto.getPreviewurl(), clipDto.getThumbnailurl(),
						clipDto.getStarttime(), clipDto.getSize(), clipDto.getDuration(), clipDto.getWidth(),
						clipDto.getHeight(), clipDto.getQrcode());
				return new ClipGoods(clipDto.getObjectid(), park, game, seat, activity, clip);
			} else {
				String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getCode();
				String message = StringUtils.hasText(dtoResult.getMessage())
						? String.format(DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getMessage(), dtoResult.getMessage())
						: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getMessage();
				log.info("\"url\":\"" + clipServiceUrl + "\"" + "\"clipId\":\"" + clipId + "\"" + "\"error\":\""
						+ message + "\"");
				throw new CustomException(code, message);
			}
		} catch (CustomException BizException) {
			throw BizException;
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"" + clipServiceUrl + "\"," + "\"goodsType\":\"" + GoodsTypeEnum.CLIP + "\","
					+ "\"goodsId\":\"" + clipId + "\"," + "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_FAILED.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_FAILED.getMessage(), ex.getMessage()));
		}
	}

	@Override
	public Goods getPhoto(String photoId) {
		try {
			HttpHeaders headers = ClientHttpHeader.createHeaders();
			PhotoDtoRequest photoDtoRequest = PhotoDtoRequest.buildImageId(photoId);
			HttpEntity<PhotoDtoRequest> request = new HttpEntity<>(photoDtoRequest, headers);
			URI url = new URI(photoServiceUrl);
			ParameterizedTypeReference<DtoResult<PhotoDto>> typeRef = new ParameterizedTypeReference<DtoResult<PhotoDto>>() {
			};
			ResponseEntity<DtoResult<PhotoDto>> jsonObjectResponseEntity = restTemplate.exchange(url, HttpMethod.POST,
					request, typeRef);
			DtoResult<PhotoDto> dtoResult = jsonObjectResponseEntity.getBody();
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null
					&& StringUtils.hasText(dtoResult.getValue().getObjectid())) {
				PhotoDto photoDto = dtoResult.getValue();
				if (!StringUtils.hasText(photoDto.getPreviewurl())) {
					throw new RuntimeException("the previewurl of photo is null.");
				}
				if (!StringUtils.hasText(photoDto.getWaitsignedurl())) {
					throw new RuntimeException("the waitsignedurl of photo is null.");
				}
				Park park = createPark(photoDto.getParkid(), photoDto.getParkname());
				Game game = createGame(photoDto.getGameid(), photoDto.getGamename());
				Seat seat = createSeat(photoDto.getSeatid());
				Activity activity = new Activity(photoDto.getActivityid());
				return new PhotoGoods(photoDto.getObjectid(), park, game, seat, activity, photoDto.getPreviewurl(),
						photoDto.getWaitsignedurl(), photoDto.getShootingtime());
			} else {
				String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.PHOTO_NOT_EXIST.getCode();
				String message = StringUtils.hasText(dtoResult.getMessage()) ? dtoResult.getMessage()
						: DomainErrorCodeEnum.PHOTO_NOT_EXIST.getMessage();
				log.info("{" + "\"url\":\"" + photoServiceUrl + "\"," + "\"photoId\":\"" + photoId + "\","
						+ "\"error\":\"" + message + "\"" + "}");
				throw new CustomException(code, message);
			}
		} catch (CustomException BizException) {
			throw BizException;
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"" + photoServiceUrl + "\"," + "\"photoId\":\"" + photoId + "\"," + "\"error\":\""
					+ ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_FAILED.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_FAILED.getMessage(), ex.getMessage()));
		}
	}

	@Override
	public Goods getPhotoProcessDownUrl(String photoId) {
		PhotoGoods goods = (PhotoGoods) getPhoto(photoId);

		return new PhotoGoods(goods.id(), goods.getPark(), goods.getGame(), goods.getSeat(), goods.getActivity(),
				goods.getPreviewUrl(), objectStorageService.convertAbsoluteUrl(goods.getDownloadUrl()));
	}

	@Override
	public String getClipIdByPhotoId(String photoId) {

		try {
			log.info(String.format("url:%s,photoId:%s", clipIdServiceUrl, photoId));
			HttpHeaders headers = ClientHttpHeader.createHeaders();
			PhotoDtoRequest photoDtoRequest = PhotoDtoRequest.buildPhotoId(photoId);
			HttpEntity<PhotoDtoRequest> request = new HttpEntity<>(photoDtoRequest, headers);
			URI url = new URI(clipIdServiceUrl);
			ParameterizedTypeReference<DtoResult<List<String>>> typeRef = new ParameterizedTypeReference<DtoResult<List<String>>>() {
			};
			ResponseEntity<DtoResult<List<String>>> jsonObjectResponseEntity = restTemplate.exchange(url,
					HttpMethod.POST, request, typeRef);
			DtoResult<List<String>> dtoResult = jsonObjectResponseEntity.getBody();
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null) {
				List<String> clipIds = dtoResult.getValue();
				if (clipIds.size() > 0) {
					return clipIds.get(0);
				} else {
					String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
							: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getCode();
					String message = StringUtils.hasText(dtoResult.getMessage()) ? dtoResult.getMessage()
							: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getMessage();
					throw new CustomException(code, message);
				}
			} else {
				String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getCode();
				String message = StringUtils.hasText(dtoResult.getMessage()) ? "core service:" + dtoResult.getMessage()
						: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getMessage();
				log.info("{" + "\"url\":\"" + clipIdServiceUrl + "\"," + "\"photoId\":\"" + photoId + "\","
						+ "\"error\":\"" + message + "\"" + "}");
				throw new CustomException(code, message);
			}
			// //TODO Mock
			// return "7f0b4e14d6614c74a5c4897d5e01f364";
		} catch (CustomException BizException) {
			throw BizException;
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"" + clipIdServiceUrl + "\"," + "\"photoId\":\"" + photoId + "\","
					+ "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_FAILED.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_FAILED.getMessage(), ex.getMessage()));
		}
	}

	@Override
	public Resource getResource(String resourceId, Integer resourceType, ResourceCategory resourceCategory) {
		try {
			log.info("{" + "\"url\":\"" + resourceAggregationUrl + "\"," + "\"resourceId\":\"" + resourceId + "\","
					+ "\"resourceType\":" + resourceType + "," + "\"resourceCategory\":" + resourceCategory + "}");
			HttpHeaders headers = ClientHttpHeader.createHeaders();
			ResourceDtoRequest resourceDtoRequest = ResourceDtoRequest.builder().resourceId(resourceId)
					.resourceType(resourceType).resourceCategory(resourceCategory.getCode()).build();
			HttpEntity<ResourceDtoRequest> request = new HttpEntity<>(resourceDtoRequest, headers);
			URI url = new URI(resourceAggregationUrl);
			ParameterizedTypeReference<DtoResult<Map<String, Object>>> typeRef = new ParameterizedTypeReference<DtoResult<Map<String, Object>>>() {
			};
			ResponseEntity<DtoResult<Map<String, Object>>> jsonObjectResponseEntity = restTemplate.exchange(url,
					HttpMethod.POST, request, typeRef);
			DtoResult<Map<String, Object>> dtoResult = jsonObjectResponseEntity.getBody();
			log.info("{" + "\"resourceId\":\"" + resourceId + "\"," + "\"result\":" + JSONObject.toJSON(dtoResult)
					+ "}");
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null
					&& dtoResult.getValue().size() > 0) {
				BaseResource resource = (BaseResource) convertToResource(dtoResult.getValue());
				resource.setResourceType(resourceType);
				return resource;
			} else {
				String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getCode();
				String message = StringUtils.hasText(dtoResult.getMessage())
						? "resource service:" + dtoResult.getMessage()
						: String.format(DomainErrorCodeEnum.VIDEOCLIPS_NOT_EXIST.getMessage(), resourceId);
				log.info("{" + "\"url\":\"" + resourceAggregationUrl + "\"," + "\"resourceId\":\"" + resourceId + "\","
						+ "\"resourceType\":" + resourceType + "," + "\"resourceCategory\":"
						+ resourceCategory.getCode() + "," + "\"error\":\"" + message + "\"" + "}");
				throw new CustomException(code, message);
			}
		} catch (CustomException BizException) {
			throw BizException;
		} catch (Exception ex) {
			log.info("{" + "\"url\":\"" + resourceAggregationUrl + "\"," + "\"resourceId\":\"" + resourceId + "\","
					+ "\"resourceType\":" + resourceType + "," + "\"resourceCategory\":" + resourceCategory.getCode()
					+ "," + "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_CORE_FAILED.getCode(),
					String.format(DomainErrorCodeEnum.CALL_CORE_FAILED.getMessage(), ex.getMessage()));
		}
	}

	private Resource convertToResource(Map<String, Object> map) {
		// "resourceid": "197824145d064f71aac020a7fe630b84",
		// "resourcetype": 1,
		// "resourcecategory": 0,
		// "waitsignedurl":
		// "result/a4f8938af26d42f0b6a28a8bb246154d/72185a5edc264b32aa8a5fc3ed50eb6c/087ee7954c514e14ab7b743ab052a00b/videos/197824145d064f71aac020a7fe630b84.mp4",
		// "previewurl":
		// "https://sioeye-disney-tmp-nx-test.s3.cn-northwest-1.amazonaws.com.cn/result/a4f8938af26d42f0b6a28a8bb246154d/72185a5edc264b32aa8a5fc3ed50eb6c/087ee7954c514e14ab7b743ab052a00b/videos/197824145d064f71aac020a7fe630b84_preview.mp4",
		// "starttime": 1575685360346,
		// "size": "859576",
		// "duration": 15,
		// "width": 864,
		// "height": 480,
		// "startrecordtime": 1575685344309,
		// "smalltype": 0,
		// "videoid": "087ee7954c514e14ab7b743ab052a00b",
		// "gamename": "轨道",
		// "gameid": "5c67c8828fc645afb5891cb7c0ea660c",
		// "seatid": "b3406694ad2d428da6de2b14977aea27",
		// "activityid": "72185a5edc264b32aa8a5fc3ed50eb6c",
		// "parkname": "spring游乐园",
		// "parkid": "a4f8938af26d42f0b6a28a8bb246154d"

		BaseResource resource = new BaseResource();
		Optional.ofNullable(map.get("resourceid")).ifPresent(o -> {
			resource.setResourceId(o.toString());
		});
		Optional.ofNullable(map.get("resourcetype")).ifPresent(o -> {
			resource.setResourceType(Integer.valueOf(o.toString()));
		});
		Optional.ofNullable(map.get("typename")).ifPresent(o -> {
			resource.setResourceName(o.toString());
		});
		Optional.ofNullable(map.get("resourcecategory")).ifPresent(o -> {
			resource.setResourceCategory(ResourceCategory.valueOf(Integer.valueOf(o.toString())));
		});
		Optional.ofNullable(map.get("waitsignedurl")).ifPresent(o -> {
			// 做完整地址
			String url = o.toString();
			if (url.startsWith("https://") || url.startsWith("http://")) {
				resource.setResourceId(o.toString());
			} else {
				resource.setDownloadUrl(objectStorageService.convertAbsoluteUrl(o.toString()));
			}
		});
		Optional.ofNullable(map.get("previewurl")).ifPresent(o -> {
			resource.setPreviewUrl(o.toString());
		});
		Optional.ofNullable(map.get("thumbnailurl")).ifPresent(o -> {
			resource.setThumbnailUrl(o.toString());
		});
		Optional.ofNullable(map.get("createtime")).ifPresent(o -> {
			resource.setCreateDate(new Date(Long.valueOf(o.toString())));
		});
		Optional.ofNullable(map.get("size")).ifPresent(o -> {
			resource.setSize(Integer.valueOf(o.toString()));
		});
		Optional.ofNullable(map.get("duration")).ifPresent(o -> {
			resource.setDuration(Float.valueOf(o.toString()));
		});
		Optional.ofNullable(map.get("width")).ifPresent(o -> {
			resource.setWidth(Integer.valueOf(o.toString()));
		});
		Optional.ofNullable(map.get("height")).ifPresent(o -> {
			resource.setHeight(Integer.valueOf(o.toString()));
		});
		Optional.ofNullable(map.get("gamename")).ifPresent(o -> {
			resource.setGameName(o.toString());
		});
		Optional.ofNullable(map.get("gameid")).ifPresent(o -> {
			resource.setGameId(o.toString());
		});
		Optional.ofNullable(map.get("seatid")).ifPresent(o -> {
			resource.setSeatId(o.toString());
		});
		Optional.ofNullable(map.get("seatname")).ifPresent(o -> {
			resource.setSeatName(o.toString());
		});
		Optional.ofNullable(map.get("sequenceno")).ifPresent(o -> {
			resource.setSeatSequenceNo(o.toString());
		});
		Optional.ofNullable(map.get("groupid")).ifPresent(o -> {
			resource.setSeatId(o.toString());
		});
		Optional.ofNullable(map.get("groupname")).ifPresent(o -> {
			resource.setSeatName(o.toString());
		});

		Optional.ofNullable(map.get("activityid")).ifPresent(o -> {
			resource.setActivityId(o.toString());
		});
		Optional.ofNullable(map.get("parkname")).ifPresent(o -> {
			resource.setParkName(o.toString());
		});
		Optional.ofNullable(map.get("parkid")).ifPresent(o -> {
			resource.setParkId(o.toString());
		});
		Optional.ofNullable(map.get("shoottime")).ifPresent(o -> {
			resource.setShootTime(new Date(Long.valueOf(o.toString())));
		});

		if (resource.getThumbnailUrl() == null) {
			resource.setThumbnailUrl(resource.getPreviewUrl());
		}

		return resource;
	}

	private Game createGame(String gameId, String gameName) {
		return new Game(gameId, gameName);
	}

	private Seat createSeat(String seatId) {

		try {
			HttpHeaders headers = ClientHttpHeader.createHeaders();
			SeatDtoRequest seatDtoRequest = new SeatDtoRequest(seatId);
			HttpEntity<SeatDtoRequest> request = new HttpEntity<>(seatDtoRequest, headers);
			URI url = new URI(seatServiceUrl);
			ParameterizedTypeReference<DtoResult<SeatDto>> typeRef = new ParameterizedTypeReference<DtoResult<SeatDto>>() {
			};
			ResponseEntity<DtoResult<SeatDto>> jsonObjectResponseEntity = restTemplate.exchange(url, HttpMethod.POST,
					request, typeRef);
			DtoResult<SeatDto> dtoResult = jsonObjectResponseEntity.getBody();
			if (dtoResult != null && dtoResult.getSuccess() && dtoResult.getValue() != null) {
				SeatDto seatDto = dtoResult.getValue();
				return new Seat(seatDto.getSeatid(), seatDto.getSequenceno(), seatDto.getMark());
			} else {
				String code = StringUtils.hasText(dtoResult.getCode()) ? dtoResult.getCode()
						: DomainErrorCodeEnum.GOODS_NOT_FOUND_SEAT.getCode();
				String message = StringUtils.hasText(dtoResult.getMessage()) ? dtoResult.getMessage()
						: DomainErrorCodeEnum.GOODS_NOT_FOUND_SEAT.getMessage();
				log.info("{" + "\"url\":\"" + seatServiceUrl + "\"," + "\"seatId\":\"" + seatId + "\"," + "\"error\":\""
						+ message + "\"" + "}");
				throw new CustomException(code, message);
			}
		} catch (CustomException BizException) {
			throw BizException;
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"" + seatServiceUrl + "\"," + "\"seatId\":\"" + seatId + "\"," + "\"error\":\""
					+ ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.CALL_PARK_SEAT_FAILED.getCode(),
					String.format(DomainErrorCodeEnum.CALL_PARK_SEAT_FAILED.getMessage(), ex.getMessage()));
		}
	}

	private Park createPark(String parkId, String parkName) {
		return new Park(parkId, parkName);
	}
}
