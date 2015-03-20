package com.enonic.xp.core.impl.server;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.server.ServerInfo;

@Component
public final class ServerInfoImpl
    implements ServerInfo
{
    private BundleContext context;

    @Activate
    public void initialize( final ComponentContext context )
    {
        this.context = context.getBundleContext();
    }

    @Override
    public String getName()
    {
        return this.context.getProperty( "xp.name" );
    }

    @Override
    public File getHomeDir()
    {
        return new File( this.context.getProperty( "xp.home" ) );
    }

    @Override
    public File getInstallDir()
    {
        return new File( this.context.getProperty( "xp.install" ) );
    }
}
