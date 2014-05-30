package com.enonic.wem.admin.app;

import java.net.URL;
import java.util.List;

import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
public final class ResourceLocator
    implements BundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ResourceLocator.class );

    private final List<Bundle> bundles;

    public ResourceLocator()
    {
        this.bundles = Lists.newCopyOnWriteArrayList();
    }

    public void init( final BundleContext context )
    {
        for ( final Bundle bundle : context.getBundles() )
        {
            addBundle( bundle );
        }
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

    public URL findResource( final String name )
    {
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
