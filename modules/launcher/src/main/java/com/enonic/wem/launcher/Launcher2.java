package com.enonic.wem.launcher;

import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.ops4j.pax.url.mvn.internal.Activator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.startlevel.FrameworkStartLevel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.wem.launcher.config.ConfigLoader2;
import com.enonic.wem.launcher.config.ConfigProperties;
import com.enonic.wem.launcher.env.Environment;
import com.enonic.wem.launcher.env.EnvironmentResolver;
import com.enonic.wem.launcher.provision.ProvisionActivator;
import com.enonic.wem.launcher.util.BannerPrinter;
import com.enonic.wem.launcher.util.RequirementChecker;
import com.enonic.wem.launcher.util.SystemProperties;

public final class Launcher2
    implements SharedConstants
{
    private final SystemProperties systemProperties;

    private Environment env;

    private ConfigProperties config;

    private Felix felix;

    public Launcher2()
    {
        this.systemProperties = SystemProperties.getDefault();
    }

    private void checkRequirements()
    {
        new RequirementChecker().check();
    }

    private void resolveEnvironment()
    {
        this.env = new EnvironmentResolver( this.systemProperties ).resolve();
    }

    private void printBanner()
    {
        final BannerPrinter banner = new BannerPrinter( System.out );
        banner.printHeader();
        banner.printEnvironment( this.env );
    }

    private void loadConfiguration()
        throws Exception
    {
        final ConfigLoader2 loader = new ConfigLoader2( this.env );
        this.config = loader.load();
        this.config.putAll( this.systemProperties );
        this.config.interpolate();
    }

    private List<BundleActivator> getActivators()
    {
        final List<BundleActivator> list = Lists.newArrayList();
        list.add( new Activator() );
        list.add( new ProvisionActivator() );
        return list;
    }

    private void createFramework()
    {
        final Map<String, Object> map = Maps.newHashMap();
        map.putAll( this.config );
        map.put( SYSTEMBUNDLE_ACTIVATORS_PROP, getActivators() );
        this.felix = new Felix( map );
    }

    public void launch()
        throws Exception
    {
        checkRequirements();
        resolveEnvironment();
        printBanner();
        loadConfiguration();

        createFramework();

        this.felix.init();
        this.felix.start();

        final FrameworkStartLevel sl = this.felix.adapt( FrameworkStartLevel.class );
        sl.setInitialBundleStartLevel( 1 );
        sl.setStartLevel( 1 );

        sl.setStartLevel( 30 );
        this.felix.stop();
        this.felix.waitForStop( 0 );
    }
}
