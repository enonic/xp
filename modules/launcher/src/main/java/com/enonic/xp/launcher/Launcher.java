package com.enonic.xp.launcher;

import java.io.File;

import com.enonic.xp.launcher.config.ConfigLoader;
import com.enonic.xp.launcher.config.ConfigProperties;
import com.enonic.xp.launcher.env.Environment;
import com.enonic.xp.launcher.env.EnvironmentResolver;
import com.enonic.xp.launcher.env.RequirementChecker;
import com.enonic.xp.launcher.env.SystemProperties;
import com.enonic.xp.launcher.framework.FrameworkService;
import com.enonic.xp.launcher.logging.LogConfigurator;
import com.enonic.xp.launcher.provision.ProvisionActivator;
import com.enonic.xp.launcher.util.BannerPrinter;

public final class Launcher
    implements SharedConstants
{
    private final SystemProperties systemProperties;

    private Environment env;

    private ConfigProperties config;

    private FrameworkService framework;

    public Launcher()
    {
        this.systemProperties = SystemProperties.getDefault();
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
    }

    private void printBanner()
    {
        final BannerPrinter banner = new BannerPrinter( this.env );
        banner.printBanner();
    }

    private void loadConfiguration()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( this.env );
        this.config = loader.load();
        this.config.putAll( this.systemProperties );
        this.config.interpolate();
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
        addProvisionActivator();
    }

    private void addProvisionActivator()
    {
        final File systemDir = new File( this.env.getInstallDir(), "system" );
        final ProvisionActivator activator = new ProvisionActivator( systemDir );
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
        createFramework();

        this.framework.start();
    }

    public void stop()
    {
        this.framework.stop();
    }
}
