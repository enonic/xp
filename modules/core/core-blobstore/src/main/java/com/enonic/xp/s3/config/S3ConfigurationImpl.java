package com.enonic.xp.s3.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.s3")
public class S3ConfigurationImpl
{
    private Configuration config;

    public String getBucketName()
    {
        return this.config.get( "bucket.name" );
    }

    public String getAccessKey()
    {
        return this.config.get( "credentials.aws_access_key_id" );
    }

    public String getSecretAccessKey()
    {
        return this.config.get( "credentials.aws_secret_access_key" );
    }

    public String getEndpoint()
    {
        return this.config.get( "connection.endpoint" );
    }

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }
}
