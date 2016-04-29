package com.enonic.xp.admin.impl.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Component
public final class ResourceLocatorImpl
    implements ResourceLocator, BundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ResourceLocatorImpl.class );

    private final List<Bundle> bundles;

    private BundleContext context;

    private File resourcesSrcDir;

    private File resourcesTargetDir;

    public ResourceLocatorImpl()
    {
        this.bundles = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public boolean shouldCache()
    {
        final boolean devMode = "true".equals( this.context.getProperty( "xp.dev.mode" ) );
        return !devMode;
    }

    @Activate
    public void start( final ComponentContext context )
    {
        this.context = context.getBundleContext();
        this.context.addBundleListener( this );
        for ( final Bundle bundle : this.context.getBundles() )
        {
            addBundle( bundle );
        }

        setResourcesDevDir();
    }

    private void setResourcesDevDir()
    {
        final String rootDir = this.context.getProperty( "xp.dev.projectDir" );
        if ( Strings.isNullOrEmpty( rootDir ) )
        {
            return;
        }

        this.resourcesSrcDir = new File( rootDir, "modules/admin/admin-ui/src/main/resources" );
        this.resourcesTargetDir = new File( rootDir, "modules/admin/admin-ui/target/resources/main" );

        LOG.info( "Loading UI resources from bundle and project directory {} and {}", this.resourcesSrcDir, this.resourcesTargetDir );
    }

    @Deactivate
    public void stop()
    {
        this.context.removeBundleListener( this );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();
        if ( event.getType() == BundleEvent.STARTED )
        {
            addBundle( bundle );
        }
        else
        {
            removeBundle( bundle );
        }
    }

    private void addBundle( final Bundle bundle )
    {
        if ( this.bundles.contains( bundle ) )
        {
            return;
        }

        if ( bundle.getResource( "/web" ) == null )
        {
            return;
        }

        this.bundles.add( bundle );
        LOG.debug( "Added web resource bundle [" + bundle.toString() + "]" );
    }

    private void removeBundle( final Bundle bundle )
    {
        if ( this.bundles.remove( bundle ) )
        {
            LOG.debug( "Removed web resource bundle [" + bundle.toString() + "]" );
        }
    }

    @Override
    public URL findResource( final String name )
        throws IOException
    {
        final URL url1 = findFromDir( this.resourcesTargetDir, name );
        if ( url1 != null )
        {
            return url1;
        }

        final URL url2 = findFromDir( this.resourcesSrcDir, name );
        if ( url2 != null )
        {
            return url2;
        }

        for ( final Bundle bundle : this.bundles )
        {
            final URL url = bundle.getResource( name );
            if ( url != null )
            {
                return url;
            }
        }

        return null;
    }

    private URL findFromDir( final File dir, final String name )
        throws IOException
    {
        if ( dir == null )
        {
            return null;
        }

        final File file = new File( dir, name );
        if ( file.isFile() )
        {
            return file.toURI().toURL();
        }

        return null;
    }
}
