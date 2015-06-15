package com.enonic.xp.core.impl.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.url.AbstractURLStreamHandlerService;

import com.google.common.base.Strings;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.resource.ResourceKey;

import static org.osgi.framework.BundleEvent.INSTALLED;
import static org.osgi.framework.BundleEvent.UNINSTALLED;

public final class ModuleURLStreamHandler
    extends AbstractURLStreamHandlerService
{
    private BundleContext bundleContext;

    private final ConcurrentHashMap<String, Long> moduleNameToBundleIdCache;

    public ModuleURLStreamHandler()
    {
        this.moduleNameToBundleIdCache = new ConcurrentHashMap<>();
    }

    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        final String path = url.getPath();
        if ( Strings.isNullOrEmpty( path ) )
        {
            throw new MalformedURLException( "Path can not be null or empty." );
        }

        final ResourceKey key = ResourceKey.from( path );
        final Bundle bundle = getBundle( key.getModule() );

        final URL resolvedUrl = bundle.getResource( key.getPath() );
        return resolvedUrl != null ? resolvedUrl.openConnection() : null;
    }

    private Bundle getBundle( final ModuleKey key )
        throws IOException
    {
        final String moduleName = key.toString();
        final Long bundleId = this.moduleNameToBundleIdCache.computeIfAbsent( moduleName, this::findBundleId );
        if ( bundleId == null )
        {
            throw new IOException( "Module [" + key.toString() + "] does not exist" );
        }
        return this.bundleContext.getBundle( bundleId );
    }

    private Long findBundleId( final String moduleName )
    {
        final Bundle bundle = findBundle( moduleName );
        return bundle == null ? null : bundle.getBundleId();
    }

    /**
     * Find bundle by module name. If multiple matching bundles are found, return the one with higher version.
     */
    private Bundle findBundle( final String moduleName )
    {
        return Arrays.stream( this.bundleContext.getBundles() ).
            filter( bundle -> bundle.getSymbolicName().equals( moduleName ) ).
            sorted( ( b1, b2 ) -> b1.getVersion().compareTo( b2.getVersion() ) ).
            findFirst().
            orElse( null );
    }

    private void invalidateCache( final BundleEvent bundleEvent )
    {
        final int eventType = bundleEvent.getType();
        if ( ( eventType == UNINSTALLED ) || ( eventType == INSTALLED ) )
        {
            final String moduleName = bundleEvent.getBundle().getSymbolicName();
            this.moduleNameToBundleIdCache.remove( moduleName );
        }
    }

    public void initialize()
    {
        this.bundleContext.addBundleListener( this::invalidateCache );
    }
}
