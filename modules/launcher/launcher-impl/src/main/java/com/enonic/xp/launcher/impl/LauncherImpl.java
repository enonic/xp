package com.enonic.xp.launcher.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.Bundle;

import com.enonic.xp.launcher.impl.config.ConfigLoader;
import com.enonic.xp.launcher.impl.config.ConfigProperties;
import com.enonic.xp.launcher.impl.env.Environment;
import com.enonic.xp.launcher.impl.env.EnvironmentResolver;
import com.enonic.xp.launcher.impl.env.SystemProperties;
import com.enonic.xp.launcher.impl.framework.FrameworkLifecycleActor;
import com.enonic.xp.launcher.impl.framework.FrameworkLifecycleService;
import com.enonic.xp.launcher.impl.framework.FrameworkService;
import com.enonic.xp.launcher.impl.log.LogActivator;
import com.enonic.xp.launcher.impl.provision.ProvisionActivator;
import com.enonic.xp.launcher.impl.util.BannerPrinter;

public final class LauncherImpl
{
    private final String[] args;

    private final SystemProperties systemProperties;

    private final VersionInfo version;

    private final Environment env;

    private final ConfigProperties config;

    private volatile FrameworkService framework;

    private volatile ExecutorService frameworkLifecycleExecutor;

    public LauncherImpl( final String... args )
        throws Exception
    {
        this.args = args;
        applySystemPropertyArgs();
        this.systemProperties = SystemProperties.getDefault();
        this.version = VersionInfo.get();
        this.env = new EnvironmentResolver( this.systemProperties ).resolve();
        System.getProperties().putAll( this.env.getAsMap() );
        this.config = loadConfiguration();
    }

    public void start()
    {
        printBanner();
        createFramework();
        this.framework.start();
    }

    public void stop()
    {
        this.framework.stop();
        this.frameworkLifecycleExecutor.shutdownNow();
    }

    private void printBanner()
    {
        new BannerPrinter( this.env, this.version ).printBanner();
    }

    private void createFramework()
    {
        this.framework = new FrameworkService( this.config );

        this.framework.activator( new org.apache.felix.log.Activator() );
        this.framework.activator( new LogActivator() );
        this.framework.activator( new ProvisionActivator( this.env.getInstallDir().resolve( "system" ), bl -> {
            if ( System.getProperty( SharedConstants.XP_RUN_MODE ) == null )
            {
                bl.stream()
                    .map( Bundle::getSymbolicName )
                    .filter( "com.enonic.xp.app.sdk"::equals )
                    .findAny()
                    .ifPresent( _ -> System.setProperty( SharedConstants.XP_RUN_MODE, "dev" ) );
            }
        } ) );

        this.frameworkLifecycleExecutor =
            Executors.newSingleThreadExecutor( r -> Thread.ofPlatform().name( "Framework Lifecycle" ).unstarted( r ) );
        this.framework.service( FrameworkLifecycleService.class,
                                new FrameworkLifecycleService( new FrameworkLifecycleActor( framework )::accept,
                                                               frameworkLifecycleExecutor ) );
    }

    private void applySystemPropertyArgs()
    {
        for ( final String arg : this.args )
        {
            if ( arg.equalsIgnoreCase( "dev" ) )
            {
                System.setProperty( SharedConstants.XP_RUN_MODE, "dev" );
            }
            else if ( arg.startsWith( "-D" ) )
            {
                applySystemPropertyArg( arg.substring( 2 ) );
            }
        }
    }

    private void applySystemPropertyArg( final String arg )
    {
        final int pos = arg.indexOf( '=' );
        if ( pos > 0 )
        {
            System.setProperty( arg.substring( 0, pos ).trim(), arg.substring( pos + 1 ).trim() );
        }
    }

    private ConfigProperties loadConfiguration()
        throws IOException
    {
        ConfigProperties config = new ConfigLoader( this.env ).load();
        config.putAll( this.systemProperties );
        config.putAll( this.version.getAsMap() );
        config.interpolate();

        config.forEach( System::setProperty );
        return config;
    }
}
