package com.sioeye.youle.run.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {
    private AwsS3BucketProperties aeon =
            new AwsS3BucketProperties("sioeye-disney-aeon-test.s3.cn-north-1.amazonaws.com.cn","sioeye-disney-aeon-test");
    private AwsS3BucketProperties tmp =
            new AwsS3BucketProperties("sioeye-disney-tmp-test.s3.cn-north-1.amazonaws.com.cn","sioeye-disney-tmp-test");
    private AwsS3KeyProperties key;
    private String region = "cn-north-1";
    private Integer urlExpiration = 1800000;
}
