package com.enonic.xp.core.impl.app;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleEventType;
import com.enonic.xp.module.ModuleUpdatedEvent;

@Component(immediate = true)
public final class ModuleRegistryImpl
    implements ModuleRegistry, SynchronousBundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ModuleRegistryImpl.class );

    private final Map<ApplicationKey, Module> modules;

    private EventPublisher eventPublisher;

    public ModuleRegistryImpl()
    {
        this.modules = Maps.newConcurrentMap();
    }

    @Activate
    public void start( final BundleContext context )
    {
        context.addBundleListener( this );
        for ( final Bundle bundle : context.getBundles() )
        {
            if ( !isModule( bundle ) )
            {
                continue;
            }

            addBundle( bundle );

            if ( bundle.getState() == Bundle.ACTIVE )
            {
                publishModuleChangeEvent( new BundleEvent( BundleEvent.STARTED, bundle ) );
            }
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is a module when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            removeBundle( bundle );
            publishModuleChangeEvent( event );
            return;
        }

        if ( !isModule( bundle ) )
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

        publishModuleChangeEvent( event );
    }

    private boolean isModule( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && Module.isModule( bundle );
    }

    private void publishModuleChangeEvent( final BundleEvent event )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( event.getBundle() );
        final ModuleEventType state = ModuleEventType.fromBundleEvent( event );
        this.eventPublisher.publish( new ModuleUpdatedEvent( applicationKey, state ) );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            installModule( bundle );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Unable to load module " + bundle.getSymbolicName(), t );
        }
    }

    private void removeBundle( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( bundle );
        uninstallModule( applicationKey );
    }

    private void installModule( final Bundle bundle )
    {
        final Module module = Module.from( bundle );
        installModule( module );
    }

    @Override
    public Module get( final ApplicationKey key )
    {
        return this.modules.get( key );
    }

    @Override
    public Collection<Module> getAll()
    {
        return this.modules.values();
    }

    private void uninstallModule( final ApplicationKey key )
    {
        this.modules.remove( key );
    }

    private void installModule( final Module module )
    {
        this.modules.put( module.getKey(), module );
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
