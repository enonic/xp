package com.enonic.xp.server;

import java.util.Properties;

import com.google.common.annotations.Beta;

@Beta
public final class BuildInfo
{
    private final Properties props;

    public BuildInfo( final Properties props )
    {
        this.props = props;
    }

    public String getHash()
    {
        return this.props.getProperty( "xp.build.hash" );
    }

    public String getShortHash()
    {
        return this.props.getProperty( "xp.build.shortHash" );
    }

    public String getTimestamp()
    {
        return this.props.getProperty( "xp.build.timestamp" );
    }

    public String getBranch()
    {
        return this.props.getProperty( "xp.build.branch" );
    }
}
