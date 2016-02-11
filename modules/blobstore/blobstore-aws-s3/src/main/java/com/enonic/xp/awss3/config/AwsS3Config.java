package com.enonic.xp.awss3.config;

import com.enonic.xp.blob.ProviderConfig;

public interface AwsS3Config
    extends ProviderConfig
{
    String bucketName();

    String accessKey();

    String secretAccessKey();

    String endpoint();
}
