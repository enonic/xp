package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationType;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FakeCmsYamlUrlResolver;
import com.enonic.xp.core.impl.app.resolver.FilteredApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

public final class ApplicationFactory
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationFactory.class );

    private final NodeService nodeService;

    private final AppConfig appConfig;

    ApplicationFactory( final NodeService nodeService, final AppConfig appConfig )
    {
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

        final ApplicationKey appKey = ApplicationHelper.getApplicationKey( bundle );
        final ApplicationUrlResolver bundleUrlResolver = createBundleUrlResolver( bundle );

        final ApplicationUrlResolver appUrlResolver = hasNodeBackedSchema( bundle, appKey )
            // schema resources are served from nodes below the application node in system-repo,
            // the bundle's own schema resources are hidden as soon as the persisted schema exists.
            ? new MultiApplicationUrlResolver( createStaticAppNodeResolver( appKey ),
                                               new FilteredApplicationUrlResolver( bundleUrlResolver, () -> schemaResourceFilter( appKey ) ) )
            : bundleUrlResolver;

        if ( appConfig.virtual_enabled() && appConfig.virtual_schema_override() )
        {
            return new MultiApplicationUrlResolver( NodeResourceApplicationUrlResolver.forVirtualApp( appKey, nodeService ),
                                                    appUrlResolver, new FakeCmsYamlUrlResolver( appKey, nodeService ) );
        }
        else
        {
            return appUrlResolver;
        }
    }

    /**
     * The schema of an application lives in nodes when the bundle owns it ({@code type: Static} or shipping {@code cms/cms.yaml})
     * or when a schema persisted by an earlier version still exists (the bundle then contributes logic only).
     * A local application shipping {@code cms/cms.yaml} is the exception: it is never persisted and must not be shadowed
     * by the schema persisted for a global installation of the same application, so its schema comes from the bundle only.
     */
    private boolean hasNodeBackedSchema( final Bundle bundle, final ApplicationKey appKey )
    {
        final boolean hasCmsDescriptor = ApplicationHelper.hasCmsDescriptor( bundle );

        if ( hasCmsDescriptor && ApplicationHelper.isLocalApplication( bundle ) )
        {
            return false;
        }

        if ( hasCmsDescriptor || ApplicationHelper.getApplicationType( bundle ) == ApplicationType.STATIC )
        {
            return true;
        }

        try
        {
            return schemaNodeExists( appKey );
        }
        catch ( Exception e )
        {
            LOG.debug( "Unable to check persisted schema of [{}], assuming none", appKey, e );
            return false;
        }
    }

    ApplicationUrlResolver createUrlResolverBySource( final Bundle bundle, final String source )
    {
        switch ( source )
        {
            case "bundle":
                return createBundleUrlResolver( bundle );
            case "virtual":
                if ( !appConfig.virtual_enabled() )
                {
                    throw new IllegalStateException( "virtual apps are disabled" );
                }
                return NodeResourceApplicationUrlResolver.forVirtualApp( ApplicationHelper.getApplicationKey( bundle ), nodeService );
            default:
                throw new IllegalArgumentException( "invalid application resolver source: " + source );
        }
    }

    private ApplicationUrlResolver createBundleUrlResolver( final Bundle bundle )
    {
        final BundleApplicationUrlResolver bundleUrlResolver = new BundleApplicationUrlResolver( bundle );
        final ClassLoaderApplicationUrlResolver classLoaderUrlResolver = createClassLoaderUrlResolver( bundle );

        return RunMode.isDev() && classLoaderUrlResolver != null
            ? new MultiApplicationUrlResolver( classLoaderUrlResolver, bundleUrlResolver )
            : bundleUrlResolver;
    }

    private NodeResourceApplicationUrlResolver createStaticAppNodeResolver( final ApplicationKey applicationKey )
    {
        return new NodeResourceApplicationUrlResolver( applicationKey, nodeService, staticAppNodePath( applicationKey ),
                                                       ApplicationHelper::createAdminContext );
    }

    // Schema resources (descriptors and i18n phrases) must not be contributed by the bundle
    // when the persisted schema (cms node below the application node) exists in system-repo
    private Predicate<String> schemaResourceFilter( final ApplicationKey applicationKey )
    {
        final Supplier<Boolean> schemaNodeExists = Suppliers.memoize( () -> schemaNodeExists( applicationKey ) );
        return path -> !( SchemaResourcePaths.isSchemaResourcePath( path ) && schemaNodeExists.get() );
    }

    private boolean schemaNodeExists( final ApplicationKey applicationKey )
    {
        final NodePath cmsPath = new NodePath( staticAppNodePath( applicationKey ), NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) );
        return ApplicationHelper.runAsAdmin( () -> nodeService.nodeExists( cmsPath ) );
    }

    private static NodePath staticAppNodePath( final ApplicationKey applicationKey )
    {
        return new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( applicationKey.getName() ) );
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
