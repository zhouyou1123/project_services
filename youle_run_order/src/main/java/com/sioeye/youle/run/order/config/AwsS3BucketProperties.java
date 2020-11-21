package com.sioeye.youle.run.order.config;

import lombok.Data;

@Data
public class AwsS3BucketProperties {
    private String addr;
    private String bucket;

    public AwsS3BucketProperties(){}
    public AwsS3BucketProperties(String addr,String bucket){
        this.addr = addr;
        this.bucket = bucket;
    }
}
