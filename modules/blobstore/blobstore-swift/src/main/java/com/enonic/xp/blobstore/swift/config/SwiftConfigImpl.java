package com.enonic.xp.blobstore.swift.config;

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

@Component(configurationPid = "com.enonic.xp.blobstore.swift")
public class SwiftConfigImpl
    implements SwiftConfig
{

    public static final String COLLECTION_PROPERTY_NAME = "container";

    public static final String AUTH_URL = "auth.url";

    public static final String DOMAIN_ID = "domain.id";

    public static final String DOMAIN_NAME = "domain.name";

    public static final String AUTH_USER = "auth.user";

    public static final String AUTH_PASSWORD = "auth.password";

    public static final String AUTH_VERSION = "auth.version";

    public static final String PROJECT_ID = "project.id";

    public static final String PROJECT_NAME = "project.name";

    public static final String READ_THROUGH_PROVIDER = "readThrough.provider";

    public static final String READ_THROUGH_ENABLED = "readThrough.enabled";

    public static final String READ_THROUGH_SIZE_THRESHOLD = "readThrough.sizeThreshold";

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
            requiredSegments( DEFAULT_REQUIRED_SEGMENTS ).
            configName( COLLECTION_PROPERTY_NAME ).
            collectionPrefix( COLLECTION_PROPERTY_NAME + "." ).
            configuration( this.config ).
            build().
            execute();
    }

    @Override
    public String authUrl()
    {
        return this.config.get( AUTH_URL );
    }

    @Override
    public String domainId()
    {
        return this.config.get( DOMAIN_ID );
    }

    @Override
    public String domainName()
    {
        return this.config.get( DOMAIN_NAME );
    }

    @Override
    public String authUser()
    {
        return this.config.get( AUTH_USER );
    }

    @Override
    public String authPassword()
    {
        return this.config.get( AUTH_PASSWORD );
    }

    @Override
    public Integer authVersion()
    {
        return Integer.valueOf( this.config.get( AUTH_VERSION ) );
    }

    @Override
    public String projectId()
    {
        return this.config.get( PROJECT_ID );
    }

    @Override
    public String projectName()
    {
        return this.config.get( PROJECT_NAME );
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
        return checkNotEmpty( this.authUser(), this.authPassword(), this.authUrl() ) &&
            hasOneOf( projectId(), projectName() ) &&
            hasOneOf( domainId(), domainName() ) &&
            authVersion() != null;
    }

    private boolean hasOneOf( final String... values )
    {
        for ( final String value : values )
        {
            if ( !Strings.isNullOrEmpty( value ) )
            {
                return true;
            }
        }

        return false;
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
