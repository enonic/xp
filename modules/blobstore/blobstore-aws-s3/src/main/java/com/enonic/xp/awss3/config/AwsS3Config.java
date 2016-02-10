package com.enonic.xp.awss3.config;

public @interface AwsS3Config
{
    String bucketName();

    String accessKey();

    String secretAccessKey();

    String endpoint();

    boolean writeThrough() default true;

    boolean readThrough() default true;

}
