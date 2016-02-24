package com.enonic.xp.blobstore.file.config;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.blobstore.file")
public class FileBlobStoreConfigImpl
    implements FileBlobStoreConfig
{
    private Configuration config;

    @Override
    public String readThroughProvider()
    {
        return null;
    }

    @Override
    public boolean readThroughEnabled()
    {
        return false;
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

    @Override
    public File baseDir()
    {
        return getFileProperty( "baseDir" );
    }

    private File getFileProperty( final String name )
    {
        return new File( this.config.get( name ) );
    }

    @Override
    public boolean validate()
    {
        return true;
    }
}
