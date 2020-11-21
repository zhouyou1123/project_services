package com.sioeye.youle.run.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "resource.type")
public class ResourceTypeProperties {
    private List<Integer> photo;
    private List<Integer> seatClip;

    public boolean containsPhotoResourceType(Integer resourceType){
        return photo.contains(resourceType);
    }
}
