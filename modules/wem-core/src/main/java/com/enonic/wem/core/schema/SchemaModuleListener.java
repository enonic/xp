package com.enonic.wem.core.schema;

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
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.metadata.MetadataProvider;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;
import com.enonic.wem.core.schema.content.ModuleContentTypeProvider;
import com.enonic.wem.core.schema.metadata.ModuleMetadataProvider;
import com.enonic.wem.core.schema.mixin.ModuleMixinProvider;
import com.enonic.wem.core.schema.relationship.ModuleRelationshipTypeProvider;

public final class SchemaModuleListener
    implements EventListener
{
    private final ModuleService moduleService;

    private final EventFilter<ModuleUpdatedEvent> eventFilter;

    public SchemaModuleListener( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
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

            final ModuleContentTypeProvider contentTypeProvider = new ModuleContentTypeProvider( module );
            bundleContext.registerService( ContentTypeProvider.class.getName(), contentTypeProvider, null );

            final ModuleMixinProvider moduleMixinProvider = new ModuleMixinProvider( module );
            bundleContext.registerService( MixinProvider.class.getName(), moduleMixinProvider, null );

            final ModuleRelationshipTypeProvider moduleRelationshipTypeProvider = new ModuleRelationshipTypeProvider( module );
            bundleContext.registerService( RelationshipTypeProvider.class.getName(), moduleRelationshipTypeProvider, null );

            final ModuleMetadataProvider moduleMetadataProvider = new ModuleMetadataProvider( module );
            bundleContext.registerService( MetadataProvider.class.getName(), moduleMetadataProvider, null );
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
