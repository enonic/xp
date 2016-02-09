package com.enonic.xp.blobstore.config;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

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
    public File blobStoreDir()
    {
        return getFileProperty( "blobStore.dir" );
    }


    private File getFileProperty( final String name )
    {
        return new File( this.config.get( name ) );
    }

}
