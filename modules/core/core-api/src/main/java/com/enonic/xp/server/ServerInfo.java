package com.enonic.xp.server;

import java.io.File;
import java.util.Properties;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ServerInfo
{
    private final Properties props;

    public ServerInfo( final Properties props )
    {
        this.props = props;
    }

    public String getName()
    {
        return this.props.getProperty( "xp.name" );
    }

    public File getHomeDir()
    {
        return new File( this.props.getProperty( "xp.home" ) );
    }

    public File getInstallDir()
    {
        return new File( this.props.getProperty( "xp.install" ) );
    }

    public BuildInfo getBuildInfo()
    {
        return new BuildInfo( this.props );
    }

    public static ServerInfo get()
    {
        return new ServerInfo( System.getProperties() );
    }
}
