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

        final ApplicationKey appKey = ApplicationHelper.getApplicationKey( bundle );
        final ApplicationUrlResolver bundleUrlResolver = createBundleUrlResolver( bundle );

        return hasNodeBackedSchema( bundle )
            // schema resources are served from nodes below the application node in system-repo,
            // the bundle's own schema resources are hidden as soon as the persisted schema exists.
            ? new MultiApplicationUrlResolver( createPersistedSchemaResolver( appKey, nodeService ),
                                               new FilteredApplicationUrlResolver( bundleUrlResolver, () -> schemaResourceFilter( appKey ) ) )
            : bundleUrlResolver;
    }

    /**
     * The schema of an application lives in nodes when the bundle owns it (ships {@code cms/cms.yaml}) and is installed globally.
     * A local application is never persisted and must not be shadowed by the schema persisted for a global installation
     * of the same application, so its schema comes from the bundle only.
     */
    private static boolean hasNodeBackedSchema( final Bundle bundle )
    {
        return ApplicationHelper.hasCmsDescriptor( bundle ) && !ApplicationHelper.isLocalApplication( bundle );
    }

    /**
     * Resolver for an application whose bundle is not active: only the persisted schema is served,
     * bundle resources (controllers, assets, ...) are not available until the application is started.
     */
    ApplicationUrlResolver createInactiveUrlResolver( final Bundle bundle )
    {
        return hasNodeBackedSchema( bundle )
            ? createPersistedSchemaResolver( ApplicationHelper.getApplicationKey( bundle ), nodeService )
            : null;
    }

    ApplicationUrlResolver createUrlResolverBySource( final Bundle bundle, final String source )
    {
        if ( "bundle".equals( source ) )
        {
            return createBundleUrlResolver( bundle );
        }
        throw new IllegalArgumentException( "invalid application resolver source: " + source );
    }

    private ApplicationUrlResolver createBundleUrlResolver( final Bundle bundle )
    {
        final BundleApplicationUrlResolver bundleUrlResolver = new BundleApplicationUrlResolver( bundle );
        final ClassLoaderApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );

        return RunMode.isDev() && classLoaderUrlResolver != null
            ? new MultiApplicationUrlResolver( classLoaderUrlResolver, bundleUrlResolver )
            : bundleUrlResolver;
    }

    /**
     * Resolver serving the schema persisted below the application node in system-repo.
     */
    static NodeResourceApplicationUrlResolver createPersistedSchemaResolver( final ApplicationKey applicationKey,
                                                                            final NodeService nodeService )
    {
        return new NodeResourceApplicationUrlResolver( applicationKey, nodeService,
                                                       ApplicationRepoServiceImpl.applicationNodePath( applicationKey ),
                                                       ApplicationHelper::createAdminContext );
    }

    // Schema resources (application descriptor and icon, cms descriptors, schema icons and i18n phrases) must not be
    // contributed by the bundle when the persisted schema (cms node below the application node) exists in system-repo
    private Predicate<String> schemaResourceFilter( final ApplicationKey applicationKey )
    {
        final Supplier<Boolean> schemaNodeExists = Suppliers.memoize( () -> schemaNodeExists( applicationKey ) );
        return path -> !( SchemaResourcePaths.isSchemaResourcePath( path ) && schemaNodeExists.get() );
    }

    private boolean schemaNodeExists( final ApplicationKey applicationKey )
    {
        final NodePath cmsPath =
            new NodePath( ApplicationRepoServiceImpl.applicationNodePath( applicationKey ), NodeName.from( SchemaResourceNames.CMS_ROOT_NAME ) );
        return ApplicationHelper.runAsAdmin( () -> nodeService.nodeExists( cmsPath ) );
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
