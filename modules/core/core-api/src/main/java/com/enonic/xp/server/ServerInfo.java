package com.enonic.xp.server;

import java.io.File;
import java.util.Properties;

import com.google.common.annotations.Beta;

@Beta
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

    public RunMode getRunMode()
    {
        try
        {
            final String value = this.props.getProperty( "xp.runMode", RunMode.PROD.toString() );
            return RunMode.valueOf( value.toUpperCase() );
        }
        catch ( final Exception e )
        {
            return RunMode.PROD;
        }
    }

    public static ServerInfo get()
    {
        return new ServerInfo( System.getProperties() );
    }
}
