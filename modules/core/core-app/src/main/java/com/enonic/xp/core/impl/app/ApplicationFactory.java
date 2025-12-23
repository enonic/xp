package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FakeSiteXmlUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

public final class ApplicationFactory
{
    private final RunMode runMode;

    private final NodeService nodeService;

    private final AppConfig appConfig;

    ApplicationFactory( final RunMode runMode, final NodeService nodeService, final AppConfig appConfig )
    {
        this.runMode = runMode;
        this.nodeService = nodeService;
        this.appConfig = appConfig;
    }

    public ApplicationImpl create( final Bundle bundle )
    {
        return new ApplicationImpl( bundle, createUrlResolver( bundle, null ), null );
    }

    ApplicationUrlResolver createUrlResolver( final Bundle bundle, final String source )
    {
        if ( source != null )
        {
            return createUrlResolverBySource( bundle, source );
        }

        final BundleApplicationUrlResolver bundleUrlResolver = new BundleApplicationUrlResolver( bundle );
        final ApplicationKey appKey = ApplicationHelper.getApplicationKey( bundle );
        final NodeResourceApplicationUrlResolver nodeResourceApplicationResolver =
            new NodeResourceApplicationUrlResolver( appKey, nodeService );
        final ClassLoaderApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );
        final FakeSiteXmlUrlResolver fakeSiteXmlUrlResolver = new FakeSiteXmlUrlResolver( appKey, nodeService );

        final boolean addCLR = RunMode.DEV.equals( this.runMode ) && classLoaderUrlResolver != null;

        if ( appConfig.virtual_enabled() && appConfig.virtual_schema_override() )
        {
            return addCLR ? new MultiApplicationUrlResolver( nodeResourceApplicationResolver, classLoaderUrlResolver, bundleUrlResolver,
                                                             fakeSiteXmlUrlResolver )
                : new MultiApplicationUrlResolver( nodeResourceApplicationResolver, bundleUrlResolver, fakeSiteXmlUrlResolver );
        }
        else
        {
            return addCLR ? new MultiApplicationUrlResolver( classLoaderUrlResolver, bundleUrlResolver ) : bundleUrlResolver;
        }
    }

    ApplicationUrlResolver createUrlResolverBySource( final Bundle bundle, final String source )
    {
        switch ( source )
        {
            case "bundle":
                final ClassLoaderApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );
                final boolean addCLR = RunMode.DEV.equals( this.runMode ) && classLoaderUrlResolver != null;

                return addCLR
                    ? new MultiApplicationUrlResolver( classLoaderUrlResolver, new BundleApplicationUrlResolver( bundle ) )
                    : new BundleApplicationUrlResolver( bundle );
            case "virtual":
                if ( !appConfig.virtual_enabled() )
                {
                    throw new IllegalStateException( "virtual apps are disabled" );
                }
                return new NodeResourceApplicationUrlResolver( ApplicationHelper.getApplicationKey( bundle ), nodeService );
            default:
                throw new IllegalArgumentException( "invalid application resolver source: " + source );
        }
    }

    private ClassLoaderApplicationUrlResolver createClassLoaderUrlResolver( final Bundle bundle )
    {
        final List<String> sourcePaths = ApplicationHelper.getSourcePaths( bundle );

        if ( sourcePaths.isEmpty() )
        {
            return null;
        }
        final List<URL> urls = getSearchPathUrls( sourcePaths );
        return new ClassLoaderApplicationUrlResolver( new URLClassLoader( urls.toArray( URL[]::new ), null ),
                                                      ApplicationHelper.getApplicationKey( bundle ) );
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
}
