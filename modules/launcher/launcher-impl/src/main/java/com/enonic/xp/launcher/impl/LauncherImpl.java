package com.enonic.xp.launcher.impl;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.enonic.xp.launcher.Launcher;
import com.enonic.xp.launcher.VersionInfo;
import com.enonic.xp.launcher.impl.config.ConfigLoader;
import com.enonic.xp.launcher.impl.config.ConfigProperties;
import com.enonic.xp.launcher.impl.env.Environment;
import com.enonic.xp.launcher.impl.env.EnvironmentResolver;
import com.enonic.xp.launcher.impl.env.SystemProperties;
import com.enonic.xp.launcher.impl.framework.FrameworkLifecycleActor;
import com.enonic.xp.launcher.impl.framework.FrameworkLifecycleService;
import com.enonic.xp.launcher.impl.framework.FrameworkService;
import com.enonic.xp.launcher.impl.log.Activator;
import com.enonic.xp.launcher.impl.provision.ProvisionActivator;
import com.enonic.xp.launcher.impl.util.BannerPrinter;

public final class LauncherImpl
    implements Launcher
{
    private final String[] args;

    private final SystemProperties systemProperties;

    private final VersionInfo version;

    private Environment env;

    private ConfigProperties config;

    private FrameworkService framework;

    private ExecutorService frameworkLifecycleExecutor;

    public LauncherImpl( final String... args )
    {
        this.args = args;
        applySystemPropertyArgs();
        this.systemProperties = SystemProperties.getDefault();
        this.version = VersionInfo.get();
    }

    private void resolveEnv()
    {
        final EnvironmentResolver resolver = new EnvironmentResolver( this.systemProperties );
        this.env = resolver.resolve();

        System.getProperties().putAll( this.env.getAsMap() );
    }

    private void printBanner()
    {
        final BannerPrinter banner = new BannerPrinter( this.env, this.version );
        banner.printBanner();
    }

    private void loadConfiguration()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( this.env );
        this.config = loader.load();
        this.config.putAll( this.systemProperties );
        this.config.putAll( this.version.getAsMap() );
        this.config.interpolate();
    }

    private void applyConfigToSystemProperties()
    {
        for ( final Map.Entry<String, String> entry : this.config.entrySet() )
        {
            System.setProperty( entry.getKey(), entry.getValue() );
        }
    }

    private void createFramework()
    {
        this.framework = new FrameworkService( this.config );

        addLoggingActivator();
        addProvisionActivator();
        setupLifecycleService();
    }

    private void addProvisionActivator()
    {
        final Path systemDir = this.env.getInstallDir().resolve( "system" );
        final ProvisionActivator activator = new ProvisionActivator( systemDir );
        this.framework.activator( activator );
    }

    private void addLoggingActivator()
    {
        this.framework.activator( new org.apache.felix.log.Activator() );
        this.framework.activator( new Activator() );
    }

    private void setupLifecycleService()
    {
        this.frameworkLifecycleExecutor = Executors.newSingleThreadExecutor( r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread( r );
            thread.setName( "Framework Lifecycle" );
            return thread;
        } );

        this.framework.service( FrameworkLifecycleService.class,
                                new FrameworkLifecycleService( new FrameworkLifecycleActor( framework )::accept,
                                                               frameworkLifecycleExecutor ) );
    }

    @Override
    public void start()
        throws Exception
    {
        resolveEnv();
        printBanner();
        loadConfiguration();
        applyConfigToSystemProperties();
        createFramework();

        this.framework.start();
    }

    @Override
    public void stop()
    {
        this.framework.stop();
        this.frameworkLifecycleExecutor.shutdownNow();
    }

    private void applySystemPropertyArgs()
    {
        for ( final String arg : this.args )
        {
            if ( arg.equalsIgnoreCase( "dev" ) )
            {
                setRunMode( "dev" );
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

    private void setRunMode( final String mode )
    {
        System.setProperty( SharedConstants.XP_RUN_MODE, mode );
    }
}
