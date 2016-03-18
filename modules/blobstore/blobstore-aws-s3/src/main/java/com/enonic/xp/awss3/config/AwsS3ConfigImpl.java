package com.enonic.xp.awss3.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentsMapFactory;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.ByteSizeParser;

@Component(configurationPid = "com.enonic.xp.blobstore.s3")
public class AwsS3ConfigImpl
    implements AwsS3Config
{
    public static final Segment[] REQUIRED_SEGMENTS = new Segment[]{Segment.from( "node" ), Segment.from( "binary" )};

    public static final String COLLECTION_PROPERTY_NAME = "bucket";

    public static final String ACCESS_KEY = "accessKey";

    public static final String SECRET_ACCESS_KEY = "secretAccessKey";

    public static final String ENDPOINT = "endpoint";

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
            configuration( this.config ).
            configName( COLLECTION_PROPERTY_NAME ).
            requiredSegments( REQUIRED_SEGMENTS ).
            build().
            execute();
    }

    @Override
    public String accessKey()
    {
        return this.config.get( ACCESS_KEY );
    }

    @Override
    public String secretAccessKey()
    {
        return this.config.get( SECRET_ACCESS_KEY );
    }

    @Override
    public String endpoint()
    {
        return this.config.get( ENDPOINT );
    }

    @Override
    public String readThroughProvider()
    {
        return this.config.get( READ_THROUGH_PROVIDER );
    }

    @Override
    public boolean readThroughEnabled()
    {
        return Boolean.valueOf( this.config.get( READ_THROUGH_ENABLED ) );
    }

    @Override
    public long readThroughSizeThreshold()
    {
        return ByteSizeParser.parse( this.config.get( READ_THROUGH_SIZE_THRESHOLD ) );
    }

    @Override
    public boolean isValid()
    {
        return checkNotEmpty( this.accessKey(), this.secretAccessKey() ) && validateSegments();
    }

    private boolean validateSegments()
    {
        final Map<Segment, String> segments = this.segments();

        for ( final Segment segment : REQUIRED_SEGMENTS )
        {
            if ( !segments.containsKey( segment ) || segments.get( segment ) == null )
            {
                return false;
            }
        }

        return true;
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

    public void setConfig( final Configuration config )
    {
        this.config = config;
    }
}
