package com.enonic.xp.repo.impl.config;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.repo")
public final class RepoConfigurationImpl
    implements RepoConfiguration
{
    private Configuration config;

    @Override
    public File getBlobStoreDir()
    {
        return getFileProperty( "blobStore.dir" );
    }

    private File getFileProperty( final String name )
    {
        return new File( this.config.get( name ) );
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
