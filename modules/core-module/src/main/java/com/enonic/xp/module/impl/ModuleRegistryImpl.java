package com.enonic.xp.module.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleEventType;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.module.ModuleVersion;

@Component(immediate = true)
public final class ModuleRegistryImpl
    implements ModuleRegistry, SynchronousBundleListener
{
    private static final String MODULE_XML = "module.xml";

    private final static Logger LOG = LoggerFactory.getLogger( ModuleRegistryImpl.class );

    private final Map<ModuleKey, Module> modules;

    private final ModuleXmlBuilder xmlSerializer;

    private EventPublisher eventPublisher;

    public ModuleRegistryImpl()
    {
        this.modules = Maps.newConcurrentMap();
        this.xmlSerializer = new ModuleXmlBuilder();
    }

    @Activate
    public void start( final ComponentContext context )
    {
        context.getBundleContext().addBundleListener( this );
        for ( final Bundle bundle : context.getBundleContext().getBundles() )
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
        if ( bundle.getState() == Bundle.UNINSTALLED )
        {
            return false;
        }
        return ( bundle.getEntry( MODULE_XML ) != null );
    }

    private void publishModuleChangeEvent( final BundleEvent event )
    {
        final ModuleKey moduleKey = ModuleKey.from( event.getBundle() );
        final ModuleEventType state = ModuleEventType.fromBundleEvent( event );
        this.eventPublisher.publish( new ModuleUpdatedEvent( moduleKey, state ) );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            installModule( bundle );
        }
        catch ( Throwable t )
        {
            LOG.warn( "Unable to load module " + bundle.getSymbolicName(), t );
        }
    }

    private void removeBundle( final Bundle bundle )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        uninstallModule( moduleKey );
    }

    private void installModule( final Bundle bundle )
    {
        final URL moduleResource = bundle.getResource( MODULE_XML );
        final String moduleXml = parseModuleXml( moduleResource );
        final ModuleBuilder moduleBuilder = new ModuleBuilder();
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        this.xmlSerializer.toModule( moduleXml, moduleBuilder, moduleKey );

        final Dictionary<String, String> headers = bundle.getHeaders();
        final String bundleDisplayName = headers.get( Constants.BUNDLE_NAME );
        final String name = bundle.getSymbolicName();
        final String displayName = bundleDisplayName != null ? bundleDisplayName : name;

        moduleBuilder.moduleKey( ModuleKey.from( bundle ) );
        moduleBuilder.moduleVersion( ModuleVersion.from( bundle.getVersion().toString() ) );
        moduleBuilder.displayName( displayName );
        moduleBuilder.bundle( bundle );

        installModule( moduleBuilder.build() );
    }

    private String parseModuleXml( final URL moduleResource )
    {
        try
        {
            return Resources.toString( moduleResource, Charsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Invalid module.xml file", e );
        }
    }

    @Override
    public Module get( final ModuleKey key )
    {
        return this.modules.get( key );
    }

    @Override
    public Collection<Module> getAll()
    {
        return this.modules.values();
    }

    private void uninstallModule( final ModuleKey key )
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
