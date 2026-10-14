package com.enonic.xp.server;

import java.io.File;
import java.util.Properties;


public final class ServerInfo
{
    public static final String DEFAULT_NAME = "xp";

    private final Properties props;

    public ServerInfo( final Properties props )
    {
        this.props = props;
    }

    /**
     * The installation's name ({@code xp.name}), {@value #DEFAULT_NAME} when none is set.
     */
    public String getName()
    {
        return this.props.getProperty( "xp.name", DEFAULT_NAME );
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
