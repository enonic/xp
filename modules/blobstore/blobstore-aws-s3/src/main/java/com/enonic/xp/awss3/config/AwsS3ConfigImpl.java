package com.enonic.xp.awss3.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.blobstore.s3")
public class AwsS3ConfigImpl
    implements AwsS3Config
{
    private Configuration config;

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    @Override
    public String bucketName()
    {
        return this.config.get( "bucketName" );
    }

    @Override
    public String accessKey()
    {
        return this.config.get( "accessKey" );
    }

    @Override
    public String secretAccessKey()
    {
        return this.config.get( "secretAccessKey" );
    }

    @Override
    public String endpoint()
    {
        return this.config.get( "endpoint" );
    }

    @Override
    public String readThroughProvider()
    {
        return this.config.get( "readThrough.provider" );
    }

    @Override
    public boolean readThroughEnabled()
    {
        return Boolean.valueOf( this.config.get( "readThrough.enabled" ) );
    }

    @Override
    public boolean validate()
    {
        return checkNotEmpty( this.accessKey(), this.bucketName(), this.secretAccessKey() );
    }

    private boolean checkNotEmpty( final String... values )
    {
        for ( final String value : values )
        {
            if ( Strings.isNullOrEmpty( value ) )
            {
                return false;
            }
        }

        return true;
    }

}
