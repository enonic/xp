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
        this.resourcesTargetDir = new File( rootDir, "modules/admin/admin-ui/build/resources/main" );

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
        final File file = findDevResource( name );
        if ( file != null )
        {
            return file.toURI().toURL();
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

    private File findDevResource( final String name )
    {
        if ( this.resourcesSrcDir == null )
        {
            return null;
        }

        final File file1 = new File( this.resourcesSrcDir, name );
        final File file2 = new File( this.resourcesTargetDir, name );

        if ( file1.isFile() && file2.isFile() )
        {
            return findNewestFile( file1, file2 );
        }

        if ( file1.isFile() )
        {
            return file1;
        }

        if ( file2.isFile() )
        {
            return file2;
        }

        return null;
    }

    private File findNewestFile( final File file1, final File file2 )
    {
        return file1.lastModified() > file2.lastModified() ? file1 : file2;
    }
}
