package com.enonic.wem.launcher;

import java.io.File;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.startlevel.BundleStartLevel;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

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

        // Put this into system.properties as soon as it's stable
        this.configuration.put( "org.osgi.framework.startlevel.beginning", "3" );
    }

    private void printBanner()
    {
        System.out.println( new BannerBuilder().homeDir( this.homeDir ).build() );
    }

    private void createFramework()
    {
        this.felix = new Felix( this.configuration );
    }

    public void start()
        throws Exception
    {
        resolveHome();
        printBanner();
        loadConfiguration();
        createFramework();

        this.felix.start();

        this.felix.getBundleContext().addBundleListener( new BundleListener()
        {
            @Override
            public void bundleChanged( final BundleEvent event )
            {
                System.out.println( " -> " + event );
            }
        } );

        installBundles();

        for ( final Bundle bundle : this.felix.getBundleContext().getBundles() )
        {
            System.out.println( bundle.getSymbolicName() + " - " + ( bundle.getState() == Bundle.ACTIVE ) );
        }

        this.felix.stop();
    }

    private void installBundles()
        throws Exception
    {
        installBundle( "org.apache.felix/org.apache.felix.configadmin/1.8.0", 5 );
        installBundle( "org.ops4j.pax.logging/pax-logging-api/1.7.2", 5 );
        installBundle( "org.ops4j.pax.logging/pax-logging-service/1.7.2", 5 );
        // installBundle( "org.apache.felix/org.apache.felix.fileinstall/3.4.0", 5 );
        installBundle( "org.ops4j.pax.url/pax-url-aether/2.0.0", 5 );
    }

    private File mavenGavToFile( final String gav )
    {
        final Iterable<String> it = Splitter.on( '/' ).omitEmptyStrings().trimResults().split( gav );
        return mavenGavToFile( Iterables.toArray( it, String.class ) );
    }

    private File mavenGavToFile( final String[] gav )
    {
        Preconditions.checkArgument( gav.length == 3, "GAV shoud have 3 parts" );
        final File bundlesDir = new File( "/Users/srs/.m2/repository" );

        final File groupDir = new File( bundlesDir, gav[0].replace( '.', '/' ) );
        final File artifactDir = new File( groupDir, gav[1] );
        final File versionDir = new File( artifactDir, gav[2] );

        return new File( versionDir, gav[1] + "-" + gav[2] + ".jar" );
    }

    private void installBundle( final String name, final int startLevel )
        throws Exception
    {
        final BundleContext context = this.felix.getBundleContext();
        final File file = mavenGavToFile( name );

        final Bundle bundle = context.installBundle( file.toURI().toString() );
        bundle.adapt( BundleStartLevel.class ).setStartLevel( startLevel );
        bundle.start();
    }

    public static void main( final String... args )
        throws Exception
    {
        /*System.setProperty( "wem.home", "/Users/srs/development/cms-homes/wem-home" );
        new Launcher().start();*/
    }
}
