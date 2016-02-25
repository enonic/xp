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
    public String authUrl()
    {
        return this.config.get( "auth.url" );
    }

    @Override
    public String domainId()
    {
        return this.config.get( "domain.id" );
    }

    @Override
    public String domainName()
    {
        return this.config.get( "domain.name" );
    }

    @Override
    public String authUser()
    {
        return this.config.get( "auth.user" );
    }

    @Override
    public String authPassword()
    {
        return this.config.get( "auth.password" );
    }

    @Override
    public Integer authVersion()
    {
        return Integer.valueOf( this.config.get( "auth.version" ) );
    }

    @Override
    public String projectId()
    {
        return this.config.get( "project.id" );
    }

    @Override
    public String projectName()
    {
        return this.config.get( "project.name" );
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
    public boolean isValid()
    {
        return checkNotEmpty( this.authUser(), this.authPassword(), this.container(), this.authUrl() ) &&
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
