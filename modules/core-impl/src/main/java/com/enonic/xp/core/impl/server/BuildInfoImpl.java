package com.enonic.xp.core.impl.server;

import org.osgi.framework.BundleContext;

import com.enonic.xp.server.BuildInfo;

final class BuildInfoImpl
    implements BuildInfo
{
    private final BundleContext context;

    public BuildInfoImpl( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public String getHash()
    {
        return this.context.getProperty( "xp.build.hash" );
    }

    @Override
    public String getShortHash()
    {
        return this.context.getProperty( "xp.build.shortHash" );
    }

    @Override
    public String getTimestamp()
    {
        return this.context.getProperty( "xp.build.timestamp" );
    }

    @Override
    public String getBranch()
    {
        return this.context.getProperty( "xp.build.branch" );
    }
}
