package com.enonic.xp.internal.blobstore.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.ByteSizeParser;

@Component(configurationPid = "com.enonic.xp.blobstore")
public class BlobStoreConfigImpl
    implements BlobStoreConfig
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
    public String providerName()
    {
        return this.config.get( "provider" );
    }

    @Override
    public boolean cache()
    {

        return Boolean.valueOf( this.config.get( "cache.enabled" ) );
    }

    @Override
    public long cacheSizeThreshold()
    {
        return getSizeProperty( "cache.sizeThreshold" );
    }

    @Override
    public long memoryCapacity()
    {
        return getSizeProperty( "cache.memoryCapacity" );
    }


    private long getSizeProperty( final String key )
    {
        final String sizeValue = this.config.get( key );
        return ByteSizeParser.parse( sizeValue );
    }

}
