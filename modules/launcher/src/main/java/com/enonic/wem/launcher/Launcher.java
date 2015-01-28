package com.enonic.wem.launcher;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.startlevel.FrameworkStartLevel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.enonic.wem.launcher.config.ConfigLoader;
import com.enonic.wem.launcher.config.ConfigProperties;
import com.enonic.wem.launcher.home.HomeDir;
import com.enonic.wem.launcher.home.HomeResolver;
import com.enonic.wem.launcher.util.BannerBuilder;
import com.enonic.wem.launcher.util.SystemProperties;

public final class Launcher
    implements SharedConstants
{
    private final SystemProperties systemProperties;

    private HomeDir homeDir;

    private ConfigProperties configuration;

    private Felix felix;

    public Launcher()
    {
        this.systemProperties = SystemProperties.getDefault();
    }

    private void resolveHome()
    {
        final HomeResolver resolver = new HomeResolver( this.systemProperties );
        this.homeDir = resolver.resolve();
    }

    private void loadConfiguration()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( this.homeDir );
        this.configuration = loader.load();
        this.configuration.putAll( this.systemProperties );
        this.configuration.interpolate();
    }

    private void printBanner()
    {
        // System.out.println( new BannerBuilder().homeDir( this.homeDir ).build() );
    }

    private void createFramework()
    {
        final Map<String, Object> map = Maps.newHashMap();
        map.putAll( this.configuration );
        map.put( SYSTEMBUNDLE_ACTIVATORS_PROP, getActivators() );

        this.felix = new Felix( map );
    }

    private List<BundleActivator> getActivators()
    {
        final ServiceLoader<BundleActivator> services = ServiceLoader.load( BundleActivator.class );
        return ImmutableList.copyOf( services );
    }

    public void launch()
        throws Exception
    {
        resolveHome();
        printBanner();
        loadConfiguration();
        createFramework();

        this.felix.init();
        this.felix.start();

        final FrameworkStartLevel sl = this.felix.adapt( FrameworkStartLevel.class );
        sl.setInitialBundleStartLevel( 1 );
        sl.setStartLevel( 1 );

        // new ProvisionActivator().start( this.felix.getBundleContext() );

        sl.setStartLevel( 30 );
        this.felix.stop();
        this.felix.waitForStop( 0 );
    }
}
