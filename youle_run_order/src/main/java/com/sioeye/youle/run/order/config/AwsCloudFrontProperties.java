package com.sioeye.youle.run.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.cloud-front")
public class AwsCloudFrontProperties {
    private String aeon;
    private String tmp;
}
