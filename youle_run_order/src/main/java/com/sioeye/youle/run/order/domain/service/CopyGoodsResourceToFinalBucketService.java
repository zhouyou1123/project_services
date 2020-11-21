package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.domain.goods.*;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.interfaces.ObjectStorageService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class CopyGoodsResourceToFinalBucketService {
    private ObjectStorageService storageService;
    public CopyGoodsResourceToFinalBucketService(ObjectStorageService storageService){
        this.storageService = storageService;
    }

    public void copy(Order order){
        //拷贝
        //获取前缀
        List<Map<String,Object>> taskList = new ArrayList<>();
        order.beginfiling(goods -> {
            String urlPrefix = storageService.getFinalUrlPrefix(goods.getPark().id());
            String previewFinalUrl = storageService.getGoodsResourceFinalUrl(urlPrefix, goods.getPreviewUrl(), getResourceType(goods.getType(), 0));
            String thumbnailFinalUrl = storageService.getGoodsResourceFinalUrl(urlPrefix, goods.getThumbnailUrl(), getResourceType(goods.getType(), 1));
            String downloadFinalUrl = storageService.getGoodsResourceFinalUrl(urlPrefix, goods.getDownloadUrl(), getResourceType(goods.getType(), 2));
            if (StringUtils.hasText(previewFinalUrl)) {
                taskList.add(createCopeResource(goods.getPreviewUrl(), previewFinalUrl, false));
            }
            if (StringUtils.hasText(thumbnailFinalUrl)) {
                taskList.add(createCopeResource(goods.getThumbnailUrl(), thumbnailFinalUrl, false));
            }
            if (StringUtils.hasText(downloadFinalUrl)) {
                taskList.add(createCopeResource(goods.getDownloadUrl(), downloadFinalUrl, true));
            }
            return new GoodsResourceUrl(previewFinalUrl,downloadFinalUrl,thumbnailFinalUrl);
        });
        storageService.copyToFinalBucket(order.id(),taskList);

    }


    /**
     *
     *
     * @param goodsType
     * @param urlType 0 预览，1 缩略图，2下载
     * @return
     */
    private int getResourceType(Integer goodsType,int urlType){
         if (GoodsTypeEnum.PHOTO.getCode().equals(goodsType) || GoodsTypeEnum.PRINT.getCode().equals(goodsType)){
            return urlType+3;
        }else{
            return urlType;
            //throw new RuntimeException("calc good resource type is error.");
        }

    }

    private Map<String,Object> createCopeResource(String srcAddress,String dstAddress,Boolean p){
        Map<String,Object> map = new HashMap<>(3);
        map.put("src",srcAddress);
        map.put("dst",dstAddress);
        map.put("private",p);
        return map;
    }
}
