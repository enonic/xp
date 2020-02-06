package com.enonic.xp.launcher.impl;

import java.io.File;
import java.util.Map;

import com.enonic.xp.launcher.Launcher;
import com.enonic.xp.launcher.LauncherListener;
import com.enonic.xp.launcher.VersionInfo;
import com.enonic.xp.launcher.impl.config.ConfigLoader;
import com.enonic.xp.launcher.impl.config.ConfigProperties;
import com.enonic.xp.launcher.impl.env.Environment;
import com.enonic.xp.launcher.impl.env.EnvironmentResolver;
import com.enonic.xp.launcher.impl.env.RequirementChecker;
import com.enonic.xp.launcher.impl.env.SystemProperties;
import com.enonic.xp.launcher.impl.framework.FrameworkService;
import com.enonic.xp.launcher.impl.provision.ProvisionActivator;
import com.enonic.xp.launcher.impl.util.BannerPrinter;

public final class LauncherImpl
    implements SharedConstants, Launcher
{
    private final String[] args;

    private final SystemProperties systemProperties;

    private final VersionInfo version;

    private Environment env;

    private ConfigProperties config;

    private FrameworkService framework;

    private LauncherListener listener;

    public LauncherImpl( final String... args )
    {
        this.args = args;
        applySystemPropertyArgs();
        this.systemProperties = SystemProperties.getDefault();
        this.version = VersionInfo.get();
        checkRequirements();
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

    private void createFramework()
    {
        this.framework = new FrameworkService();
        this.framework.listener( this.listener );
        this.framework.config( this.config );

        addLoggingActivator();
        addProvisionActivator();
    }

    private void addProvisionActivator()
    {
        final File systemDir = new File( this.env.getInstallDir(), "system" );
        final ProvisionActivator activator = new ProvisionActivator( systemDir );
        this.framework.activator( activator );
    }

    private void addLoggingActivator()
    {
        this.framework.activator( new org.apache.felix.log.extension.Activator() );
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
        System.setProperty( XP_RUN_MODE, mode );
    }

    @Override
    public boolean hasArg( final String value )
    {
        for ( final String arg : this.args )
        {
            if ( arg.equalsIgnoreCase( value ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setListener( final LauncherListener listener )
    {
        this.listener = listener;
    }

    @Override
    public String getHttpUrl()
    {
        return this.framework.getHttpUrl();
    }

    @Override
    public File getHomeDir()
    {
        return this.env.getHomeDir();
    }
}
