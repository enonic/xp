package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;

import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FilteredApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

public final class ApplicationFactory
{
    private final NodeService nodeService;

    ApplicationFactory( final NodeService nodeService )
    {
        this.nodeService = nodeService;
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

        final ApplicationKey applicationKey = ApplicationHelper.getApplicationKey( bundle );

        return new MultiApplicationUrlResolver( new NodeResourceApplicationUrlResolver( applicationKey, nodeService ),
                                                new FilteredApplicationUrlResolver( createBundleUrlResolver( bundle ),
                                                                                    () -> schemaDescriptorFilter( applicationKey ) ) );
    }

    ApplicationUrlResolver createUrlResolverBySource( final Bundle bundle, final String source )
    {
        switch ( source )
        {
            case "bundle":
                return createBundleUrlResolver( bundle );
            case "virtual":
                return new NodeResourceApplicationUrlResolver( ApplicationHelper.getApplicationKey( bundle ), nodeService );
            default:
                throw new IllegalArgumentException( "invalid application resolver source: " + source );
        }
    }

    private ApplicationUrlResolver createBundleUrlResolver( final Bundle bundle )
    {
        final List<ApplicationUrlResolver> resolvers = new ArrayList<>();

        final ClassLoaderApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );
        if ( RunMode.isDev() && classLoaderUrlResolver != null )
        {
            resolvers.add( classLoaderUrlResolver );
        }

        resolvers.add( new BundleApplicationUrlResolver( bundle ) );

        return resolvers.size() == 1 ? resolvers.get( 0 ) : new MultiApplicationUrlResolver( resolvers.toArray( ApplicationUrlResolver[]::new ) );
    }

    // Schema descriptors must not be contributed by the bundle when the application node exists in the virtual app repo
    private Predicate<String> schemaDescriptorFilter( final ApplicationKey applicationKey )
    {
        final Supplier<Boolean> appNodeExists = Suppliers.memoize( () -> appNodeExists( applicationKey ) );
        return path -> !( SchemaResourcePaths.isSchemaDescriptorPath( path ) && appNodeExists.get() );
    }

    private boolean appNodeExists( final ApplicationKey applicationKey )
    {
        final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );
        return VirtualAppContext.createAdminContext().callWith( () -> nodeService.nodeExists( appPath ) );
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