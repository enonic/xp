package com.enonic.wem.launcher;

import org.apache.felix.framework.Felix;

import com.enonic.wem.launcher.config.ConfigLoader;
import com.enonic.wem.launcher.config.ConfigProperties;
import com.enonic.wem.launcher.home.HomeDir;
import com.enonic.wem.launcher.home.HomeResolver;
import com.enonic.wem.launcher.util.BannerBuilder;
import com.enonic.wem.launcher.util.SystemProperties;

public final class Launcher
{
    private final SystemProperties systemProperties;

    private HomeDir homeDir;

    private ConfigProperties configuration;

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
        this.configuration = this.configuration.interpolate();
    }

    private void printBanner()
    {
        System.out.println( new BannerBuilder().homeDir( this.homeDir ).build() );
    }

    public void start()
        throws Exception
    {
        resolveHome();
        printBanner();
        loadConfiguration();

        final Felix felix = new Felix( this.configuration );
        felix.start();
        felix.stop();
    }

    public static void main( final String... args )
        throws Exception
    {
        System.setProperty( "wem.home", "/Users/srs/development/cms-homes/wem-home" );
        new Launcher().start();
    }
}
