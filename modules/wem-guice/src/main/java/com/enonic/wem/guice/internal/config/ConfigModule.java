package com.enonic.wem.guice.internal.config;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.inject.name.Names;

import com.enonic.wem.guice.Configuration;
import com.enonic.wem.guice.OsgiModule;

public final class ConfigModule
    extends OsgiModule
{
    private final String pid;

    private ConfigModule( final String pid )
    {
        this.pid = pid;
    }

    @Override
    protected void configure()
    {
        bind( String.class ).annotatedWith( Names.named( Constants.SERVICE_PID ) ).toInstance( this.pid );
        bind( Configuration.class ).toProvider( ConfigurationLoader.class );
        service( ConfigurationAdmin.class ).importSingle();
        service( BundleReloader.class ).attribute( Constants.SERVICE_PID, this.pid ).export();
    }

    public static ConfigModule forPid( final String pid )
    {
        return new ConfigModule( pid );
    }
}
