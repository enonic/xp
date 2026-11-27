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

    /**
     * The server's version as it is shown to people and to clients. A snapshot version says little of what is
     * running, so it is followed by the short hash of the commit it was built from, when the build recorded one:
     * {@code 8.2.0-SNAPSHOT+b741000}. A released version stands alone. {@link VersionInfo#getVersion()} is the version
     * without the hash.
     */
    public String getVersion()
    {
        final String version = VersionInfo.get().getVersion();
        if ( !version.endsWith( "-SNAPSHOT" ) )
        {
            return version;
        }
        final String shortHash = getBuildInfo().getShortHash();
        return shortHash.isBlank() || "N/A".equals( shortHash ) ? version : version + "+" + shortHash;
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
