package com.enonic.xp.core.impl.app;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FilteredApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.node.Node;
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
        final boolean persisted = isPersisted( bundle );
        final ApplicationKey appKey = ApplicationHelper.getApplicationKey( bundle );
        // both are kept on the application node, read anew on every call: they change while the bundle stays installed
        return new ApplicationImpl( bundle, createUrlResolver( bundle, null ), null, persisted,
                                    () -> persisted && readNode( appKey, ApplicationHelper::isLocked ),
                                    () -> persisted && readNode( appKey, ApplicationHelper::isSchemaModified ) );
    }

    private boolean readNode( final ApplicationKey appKey, final Predicate<Node> flag )
    {
        final Callable<Boolean> read =
            () -> flag.test( nodeService.getByPath( ApplicationRepoServiceImpl.applicationNodePath( appKey ) ) );
        return ApplicationHelper.runAsAdmin( read );
    }

    ApplicationUrlResolver createUrlResolver( final Bundle bundle, final String source )
    {
        if ( source != null )
        {
            return createUrlResolverBySource( bundle, source );
        }

        final ApplicationKey appKey = ApplicationHelper.getApplicationKey( bundle );
        final ApplicationUrlResolver bundleUrlResolver = createBundleUrlResolver( bundle );

        return isPersisted( bundle )
            // the application descriptor and icon, and the schema of an application owning one, are served from nodes below
            // the application node in the application repository; the bundle's own copies are never read.
            ? new MultiApplicationUrlResolver( createPersistedSchemaResolver( appKey, nodeService ),
                                               new FilteredApplicationUrlResolver( bundleUrlResolver, () -> ApplicationFactory::servedFromBundle ) )
            : bundleUrlResolver;
    }

    /**
     * The descriptor and schema of a globally installed application live in nodes. A local application is never persisted
     * and must not be shadowed by what is persisted for a global installation of the same application, so everything
     * comes from its bundle. So does everything of a system application ({@code X-Bundle-Type: system}): it is provisioned by
     * the launcher, never installed through the application service, so nothing is ever persisted for it, while it ships
     * resources at the persisted paths ({@code cms/style/style.yaml}).
     */
    private static boolean isPersisted( final Bundle bundle )
    {
        return !ApplicationHelper.isLocalApplication( bundle ) && !ApplicationBundleUtils.isSystemApplication( bundle );
    }

    /**
     * Resolver for an application whose bundle is not active: the persisted descriptor and schema of a global application, the
     * descriptor and icon of a local one straight from its bundle, which is all a local application has. Bundle resources
     * (controllers, assets, ...) are not available until the application is started.
     */
    ApplicationUrlResolver createInactiveUrlResolver( final Bundle bundle )
    {
        return isPersisted( bundle )
            ? createPersistedSchemaResolver( ApplicationHelper.getApplicationKey( bundle ), nodeService )
            : new FilteredApplicationUrlResolver( createBundleUrlResolver( bundle ), () -> SchemaResourcePaths::isAppDescriptorOrIconPath );
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
     * Resolver serving the schema persisted below the application node in the application repository.
     */
    static NodeResourceApplicationUrlResolver createPersistedSchemaResolver( final ApplicationKey applicationKey,
                                                                            final NodeService nodeService )
    {
        return new NodeResourceApplicationUrlResolver( applicationKey, nodeService,
                                                       ApplicationRepoServiceImpl.applicationNodePath( applicationKey ),
                                                       ApplicationHelper::createAdminContext );
    }

    /**
     * The application descriptor and icon ({@code enonic.yaml|yml}, {@code enonic.svg} and the legacy {@code application.*}) and the
     * schema resources (cms descriptors, schema icons and i18n phrases) of a global application are persisted on install, the
     * descriptor and icon always, the schema when the application ships {@code cms/cms.yaml}. They are served from the nodes only:
     * what the bundle ships at those paths is never read, whether or not anything is persisted.
     */
    private static boolean servedFromBundle( final String path )
    {
        return !SchemaResourcePaths.isSchemaResourcePath( path );
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
