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
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

final class ApplicationFactory
{
    private final RunMode runMode;

    private final NodeService nodeService;

    ApplicationFactory( final RunMode runMode, final NodeService nodeService )
    {
        this.runMode = runMode;
        this.nodeService = nodeService;
    }

    public ApplicationImpl create( final Bundle bundle )
    {
        return create( bundle, null );
    }

    public ApplicationImpl create( final Bundle bundle, final Configuration config )
    {
        final ApplicationBuilder builder = new ApplicationBuilder();
        builder.bundle( bundle );
        builder.urlResolver( createUrlResolver( bundle ) );
        builder.config( config );
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

        final ApplicationUrlResolver nodeResourceApplicationResolver = createNodeResourceApplicationResolver( bundle );
        final ApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );

        return new MultiApplicationUrlResolver( nodeResourceApplicationResolver, classLoaderUrlResolver, bundleUrlResolver );
    }

    private ApplicationUrlResolver createClassLoaderUrlResolver( final Bundle bundle )
    {
        final List<String> sourcePaths = ApplicationHelper.getSourcePaths( bundle );

        if ( sourcePaths.isEmpty() )
        {
            return null;
        }
        final List<URL> urls = getSearchPathUrls( sourcePaths );
        return new ClassLoaderApplicationUrlResolver( new URLClassLoader( urls.toArray( URL[]::new ), null ),
                                                      ApplicationKey.from( bundle ) );
    }

    private ApplicationUrlResolver createNodeResourceApplicationResolver( final Bundle bundle )
    {
        return new NodeResourceApplicationUrlResolver( bundle, nodeService );
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
