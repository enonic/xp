package com.enonic.wem.core.schema;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventFilter;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleEventType;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.ModuleUpdatedEvent;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.core.module.ModuleServiceImpl;

@Singleton
public final class SchemaModuleListener
    implements EventListener
{
    private final ModuleServiceImpl moduleService;

    private final EventFilter<ModuleUpdatedEvent> eventFilter;

    @Inject
    public SchemaModuleListener( final ModuleService moduleService )
    {
        this.moduleService = (ModuleServiceImpl) moduleService;
        this.eventFilter = EventFilter.filterOn( ModuleUpdatedEvent.class, this::onModuleUpdatedEvent );
    }

    @Override
    public void onEvent( final Event event )
    {
        this.eventFilter.onEvent( event );
    }

    private void onModuleUpdatedEvent( final ModuleUpdatedEvent event )
    {
        if ( event.getEventType() == ModuleEventType.STARTED )
        {
            registerSchemas( event.getModuleKey() );
        }
    }

    private void registerSchemas( final ModuleKey moduleKey )
    {
        final Module module = findModule( moduleKey );
        if ( ( module != null ) && ( module.getBundle() != null ) )
        {
            final Bundle bundle = module.getBundle();
            final BundleContext bundleContext = bundle.getBundleContext();

            // TODO register only if schemas found in module
            final ModuleSchemaProvider moduleSchemaProvider = new ModuleSchemaProvider( module );
            bundleContext.registerService( SchemaProvider.class.getName(), moduleSchemaProvider, null );
        }
    }

    private Module findModule( final ModuleKey moduleKey )
    {
        try
        {
            return this.moduleService.getModule( moduleKey );
        }
        catch ( ModuleNotFoundException e )
        {
            return null;
        }
    }
}
