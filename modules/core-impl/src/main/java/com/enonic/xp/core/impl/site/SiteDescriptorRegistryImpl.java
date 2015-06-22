package com.enonic.xp.core.impl.site;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.site.SiteDescriptor;

@Component(immediate = true)
public final class SiteDescriptorRegistryImpl
    implements SiteDescriptorRegistry, SynchronousBundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( SiteDescriptorRegistryImpl.class );

    private final Map<ModuleKey, SiteDescriptor> siteDescriptorMap;

    public SiteDescriptorRegistryImpl()
    {
        this.siteDescriptorMap = Maps.newConcurrentMap();
    }

    @Activate
    public void start( final ComponentContext context )
    {
        context.getBundleContext().addBundleListener( this );
        for ( final Bundle bundle : context.getBundleContext().getBundles() )
        {
            if ( !isSite( bundle ) )
            {
                continue;
            }

            addBundle( bundle );
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is a site when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            removeBundle( bundle );
            return;
        }

        if ( !isSite( bundle ) )
        {
            return;
        }

        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;
        }
    }

    @Override
    public SiteDescriptor get( final ModuleKey moduleKey )
    {
        return this.siteDescriptorMap.get( moduleKey );
    }

    private boolean isSite( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && SiteDescriptorBuilder.isSite( bundle );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            installSiteDescriptor( bundle );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Unable to load site descriptor for " + bundle.getSymbolicName(), t );
        }
    }

    private void installSiteDescriptor( final Bundle bundle )
    {
        final SiteDescriptorBuilder builder = new SiteDescriptorBuilder();
        builder.bundle( bundle );

        final SiteDescriptor siteDescriptor = builder.build();
        installSiteDescriptor( ModuleKey.from( bundle ), siteDescriptor );
    }

    private void installSiteDescriptor( final ModuleKey moduleKey, final SiteDescriptor siteDescriptor )
    {
        this.siteDescriptorMap.put( moduleKey, siteDescriptor );
    }

    private void removeBundle( final Bundle bundle )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        uninstallSiteDescriptor( moduleKey );
    }

    private void uninstallSiteDescriptor( final ModuleKey moduleKey )
    {
        this.siteDescriptorMap.remove( moduleKey );
    }
}
