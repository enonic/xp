package com.enonic.xp.launcher;

import java.io.File;
import java.util.Map;

import com.enonic.xp.launcher.config.ConfigLoader;
import com.enonic.xp.launcher.config.ConfigProperties;
import com.enonic.xp.launcher.env.Environment;
import com.enonic.xp.launcher.env.EnvironmentResolver;
import com.enonic.xp.launcher.env.RequirementChecker;
import com.enonic.xp.launcher.env.SystemProperties;
import com.enonic.xp.launcher.framework.FrameworkService;
import com.enonic.xp.launcher.logging.LogActivator;
import com.enonic.xp.launcher.logging.LogConfigurator;
import com.enonic.xp.launcher.provision.ProvisionActivator;
import com.enonic.xp.launcher.util.BannerPrinter;
import com.enonic.xp.launcher.util.BuildVersionInfo;
import com.enonic.xp.launcher.watch.WatchActivator;

public final class Launcher
    implements SharedConstants
{
    private final String[] args;

    private final SystemProperties systemProperties;

    private final BuildVersionInfo version;

    private Environment env;

    private ConfigProperties config;

    private FrameworkService framework;

    public Launcher( final String... args )
    {
        this.args = args;
        System.setProperty( "java.awt.headless", "true" );
        applySystemPropertyArgs();
        this.systemProperties = SystemProperties.getDefault();
        this.version = BuildVersionInfo.load();
    }

    private void checkRequirements()
    {
        new RequirementChecker( this.systemProperties ).check();
    }

    private void resolveEnv()
    {
        final EnvironmentResolver resolver = new EnvironmentResolver( this.systemProperties );
        this.env = resolver.resolve();
        this.env.validate();

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

    private void setupLogging()
    {
        final LogConfigurator configurator = new LogConfigurator( this.env );
        configurator.configure();
    }

    private void createFramework()
    {
        this.framework = new FrameworkService();
        this.framework.config( this.config );

        addLogActivator();
        addWatchActivator();
        addProvisionActivator();
    }

    private void addLogActivator()
    {
        final LogActivator activator = new LogActivator();
        this.framework.activator( activator );
    }

    private void addWatchActivator()
    {
        final WatchActivator activator = new WatchActivator();
        this.framework.activator( activator );
    }

    private void addProvisionActivator()
    {
        final File systemDir = new File( this.env.getInstallDir(), "system" );
        final ProvisionActivator activator = new ProvisionActivator( systemDir, this.config );
        this.framework.activator( activator );
    }

    public void start()
        throws Exception
    {
        checkRequirements();
        resolveEnv();
        printBanner();
        setupLogging();
        loadConfiguration();
        applyConfigToSystemProperties();
        createFramework();

        this.framework.start();
    }

    public void stop()
    {
        this.framework.stop();
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
        System.setProperty( "xp.runMode", mode );
    }
}
