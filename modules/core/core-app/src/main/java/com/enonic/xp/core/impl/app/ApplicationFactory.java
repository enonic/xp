package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.config.ApplicationConfigMap;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.server.RunMode;

final class ApplicationFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationFactory.class );

    private final static Configuration EMPTY_CONFIG = ConfigBuilder.create().build();

    private final RunMode runMode;

    ApplicationFactory( final RunMode runMode )
    {
        this.runMode = runMode;
    }

    public Application create( final Bundle bundle )
    {
        if ( !ApplicationHelper.isApplication( bundle ) )
        {
            return null;
        }

        final ApplicationBuilder builder = new ApplicationBuilder();
        builder.bundle( bundle );
        builder.urlResolver( createUrlResolver( bundle ) );
        builder.config( getConfig( bundle ) );
        return builder.build();
    }

    ApplicationUrlResolver createUrlResolver( final Bundle bundle )
    {
        final ApplicationUrlResolver bundleUrlResolver = new BundleApplicationUrlResolver( bundle );
        if ( this.runMode != RunMode.DEV )
        {
            return bundleUrlResolver;
        }

        final List<String> sourcePaths = ApplicationHelper.getSourcePaths( bundle );
        if ( sourcePaths.isEmpty() )
        {
            return bundleUrlResolver;
        }

        final ApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( sourcePaths );

        return new MultiApplicationUrlResolver( classLoaderUrlResolver, bundleUrlResolver );
    }

    private ApplicationUrlResolver createClassLoaderUrlResolver( final List<String> paths )
    {
        final List<URL> urls = getSearchPathUrls( paths );
        return ClassLoaderApplicationUrlResolver.create( urls );
    }

    private List<URL> getSearchPathUrls( final List<String> paths )
    {
        final List<URL> result = new ArrayList<>();
        for ( final String path : paths )
        {
            final URL url = getSearchPathUrl( path );
            if ( url != null )
            {
                result.add( url );
            }
        }

        return result;
    }

    private URL getSearchPathUrl( final String path )
    {
        try
        {
            final Path file = Path.of( path );
            return file.toUri().toURL();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private Configuration getConfig( final Bundle bundle )
    {
        final ApplicationKey key = ApplicationKey.from( bundle );
        Configuration config = ApplicationConfigMap.INSTANCE.get( key );

        if ( config != null )
        {
            return config;
        }

        config = loadConfig( bundle );
        if ( config == null )
        {
            config = EMPTY_CONFIG;
        }
        else
        {
            ApplicationConfigMap.INSTANCE.put( key, config );
        }

        return config;
    }

    private Configuration loadConfig( final Bundle bundle )
    {
        BundleContext ctx = null;
        ServiceReference<ConfigurationAdmin> serviceRef = null;

        try
        {
            ctx = bundle.getBundleContext();
            if ( ctx == null )
            {
                return null;
            }
            serviceRef = ctx.getServiceReference( ConfigurationAdmin.class );
            ConfigurationAdmin configAdmin = ctx.getService( serviceRef );

            org.osgi.service.cm.Configuration conf = configAdmin.getConfiguration( bundle.getSymbolicName() );
            final Dictionary<String, Object> properties = conf.getProperties();
            return properties == null ? null : ConfigBuilder.create().addAll( properties ).build();
        }
        catch ( Exception e )
        {
            LOG.warn( "Unable to load app configuration for " + bundle.getSymbolicName(), e );
            return null;
        }
        finally
        {
            if ( ctx != null && serviceRef != null )
            {
                ctx.ungetService( serviceRef );
            }
        }
    }
}
