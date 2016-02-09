package com.enonic.xp.blobstore.awss3.config;

public @interface S3Configuration
{
    String getBucketName();

    String getAccessKey();

    String getSecretAccessKey();

    String getEndpoint();

    boolean isWriteThrough() default true;

    boolean isReadThrough() default true;

}
