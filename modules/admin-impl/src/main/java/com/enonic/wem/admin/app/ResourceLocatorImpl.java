package com.enonic.wem.admin.app;

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

@Component(configurationPid = "com.enonic.wem.admin")
public final class ResourceLocatorImpl
    implements ResourceLocator, BundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ResourceLocatorImpl.class );

    private final List<Bundle> bundles;

    private BundleContext context;

    private File resourcesDevDir;

    public ResourceLocatorImpl()
    {
        this.bundles = Lists.newCopyOnWriteArrayList();
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

        setResourcesDevDir( (String) context.getProperties().get( "resourcesDevDir" ) );
    }

    private void setResourcesDevDir( final String dir )
    {
        this.resourcesDevDir = Strings.isNullOrEmpty( dir ) ? null : new File( dir );
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
        if ( this.resourcesDevDir != null )
        {
            final File file = new File( this.resourcesDevDir, name );
            if ( file.isFile() )
            {
                return file.toURI().toURL();
            }
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
}
