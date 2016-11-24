package com.enonic.xp.core.impl.app;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.config.ApplicationConfigMap;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.server.RunMode;

final class ApplicationFactory
{
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

        final ApplicationKey key = ApplicationKey.from( bundle.getSymbolicName() );
        builder.config( ApplicationConfigMap.INSTANCE.get( key ) );
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
