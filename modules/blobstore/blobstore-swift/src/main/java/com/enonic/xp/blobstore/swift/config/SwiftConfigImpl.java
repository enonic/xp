package com.enonic.xp.blobstore.swift.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.blobstore.swift")
public class SwiftConfigImpl
    implements SwiftConfig
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
    public String container()
    {
        return this.config.get( "container" );
    }

    @Override
    public String endpoint()
    {
        return this.config.get( "endpoint" );
    }

    @Override
    public String domain()
    {
        return this.config.get( "domain" );
    }

    @Override
    public String user()
    {
        return this.config.get( "user" );
    }

    @Override
    public String password()
    {
        return this.config.get( "password" );
    }

    @Override
    public String projectId()
    {
        return this.config.get( "projectId" );
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
        return checkNotEmpty( this.user(), this.container(), this.domain(), this.password(), this.projectId(), this.endpoint() );
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
