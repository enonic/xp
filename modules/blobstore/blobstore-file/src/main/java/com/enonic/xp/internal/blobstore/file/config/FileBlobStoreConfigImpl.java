package com.enonic.xp.internal.blobstore.file.config;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.internal.blobstore.file.SegmentsMapFactory;
import com.enonic.xp.util.ByteSizeParser;

@Component(configurationPid = "com.enonic.xp.blobstore.file")
public final class FileBlobStoreConfigImpl
    implements FileBlobStoreConfig
{
    private static final String BASE_DIR = "baseDir";

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
    public Map<Segment, String> segments()
    {
        return SegmentsMapFactory.create().
            configuration( config ).
            configName( BASE_DIR ).
            collectionPrefix( "baseDir" + "." ).
            requiredSegments( Segment.DEFAULT_REQUIRED_SEGMENTS ).
            build().
            execute();
    }

    @Override
    public long readThroughSizeThreshold()
    {
        return ByteSizeParser.parse( this.config.get( "readThrough.sizeThreshold" ) );
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
    public File baseDir()
    {
        return getFileProperty( "baseDir" );
    }

    private File getFileProperty( final String name )
    {
        return new File( this.config.get( name ) );
    }

    @Override
    public boolean isValid()
    {
        return true;
    }
}
