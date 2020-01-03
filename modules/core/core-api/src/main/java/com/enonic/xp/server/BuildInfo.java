package com.enonic.xp.server;

import java.util.Properties;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class BuildInfo
{
    private final static String NA = "N/A";

    private final Properties props;

    public BuildInfo( final Properties props )
    {
        this.props = props;
    }

    public String getHash()
    {
        return this.props.getProperty( "xp.build.hash", NA );
    }

    public String getShortHash()
    {
        return this.props.getProperty( "xp.build.shortHash", NA );
    }

    public String getTimestamp()
    {
        return this.props.getProperty( "xp.build.timestamp", NA );
    }

    public String getBranch()
    {
        return this.props.getProperty( "xp.build.branch", NA );
    }
}
