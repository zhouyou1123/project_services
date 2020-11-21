package com.sioeye.youle.run.order.gateways.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class PhotoDtoRequest {
    private String imageid;
    private String photoid;
    private  PhotoDtoRequest(){}
    private PhotoDtoRequest(String imageId,String photoId){
        this.imageid =imageId;
        this.photoid = photoId;
    }
    public static PhotoDtoRequest buildImageId(String imageId){
        return new PhotoDtoRequest(imageId,null);
    }
    public static PhotoDtoRequest buildPhotoId(String photoId){
        return new PhotoDtoRequest(null,photoId);
    }
}
