package com.enonic.xp.awss3.config;

import java.util.Map;

import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blob.Segment;

public interface AwsS3Config
    extends ProviderConfig
{
    Map<Segment, String> segments();

    String accessKey();

    String secretAccessKey();

    String endpoint();
}
