package com.enonic.xp.core.impl.app;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.server.RunMode;

final class ApplicationFactory
{
    private final RunMode runMode;

    public ApplicationFactory( final RunMode runMode )
    {
        this.runMode = runMode;
    }

    public Application create( final Bundle bundle )
    {
        if ( !ApplicationHelper.isApplication( bundle ) )
        {
            return null;
        }

        final ApplicationUrlResolver urlResolver = createUrlResolver( bundle );
        return new ApplicationImpl( bundle, urlResolver );
    }

    protected ApplicationUrlResolver createUrlResolver( final Bundle bundle )
    {
        final ApplicationUrlResolver bundleUrlResolver = new BundleApplicationUrlResolver( bundle );
        if ( this.runMode != RunMode.DEV )
        {
            return bundleUrlResolver;
        }

        final List<String> sourcePaths = ApplicationHelper.getSourcePaths( bundle );
        final ApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( sourcePaths );

        return new MultiApplicationUrlResolver( bundleUrlResolver, classLoaderUrlResolver );
    }

    private ApplicationUrlResolver createClassLoaderUrlResolver( final List<String> paths )
    {
        final List<URL> urls = getSearchPathUrls( paths );
        final URLClassLoader loader = new URLClassLoader( urls.toArray( new URL[urls.size()] ), null );
        return new ClassLoaderApplicationUrlResolver( loader );
    }

    private List<URL> getSearchPathUrls( final List<String> paths )
    {
        final List<URL> result = Lists.newArrayList();
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
            final File file = new File( path );
            return file.toURI().toURL();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
