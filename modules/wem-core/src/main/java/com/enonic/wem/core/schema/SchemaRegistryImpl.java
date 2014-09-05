package com.enonic.wem.core.schema;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.google.inject.Inject;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaName;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static java.util.stream.Collectors.toList;

public final class SchemaRegistryImpl
    implements ServiceTrackerCustomizer, SchemaRegistry
{

    private BundleContext bundleContext;

    private ServiceTracker serviceTracker;

    private ConcurrentHashMap<SchemaName, Schema> allSchemas;

    private ConcurrentHashMap<ModuleKey, Schemas> moduleSchemas;

    public SchemaRegistryImpl()
    {
        this.allSchemas = new ConcurrentHashMap<>();
        this.moduleSchemas = new ConcurrentHashMap<>();
    }

    @Inject
    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void start()
    {
        this.serviceTracker = new ServiceTracker( this.bundleContext, SchemaProvider.class.getName(), this );
        this.serviceTracker.open();
    }

    public void stop()
    {
        this.serviceTracker.close();
    }

    @Override
    public Object addingService( final ServiceReference serviceReference )
    {
        final SchemaProvider schemaProvider = (SchemaProvider) this.bundleContext.getService( serviceReference );

        final Schemas schemas = schemaProvider.getSchemas();
        addSchemas( serviceReference.getBundle(), schemas );

        return schemaProvider;
    }

    @Override
    public void modifiedService( final ServiceReference serviceReference, final Object service )
    {
        final SchemaProvider schemaProvider = (SchemaProvider) service;
        final Schemas schemas = schemaProvider.getSchemas();
        removeBundleSchemas( serviceReference.getBundle() );
        addSchemas( serviceReference.getBundle(), schemas );
    }

    @Override
    public void removedService( final ServiceReference serviceReference, final Object service )
    {
        removeBundleSchemas( serviceReference.getBundle() );
    }

    private void addSchemas( final Bundle bundle, final Schemas schemas )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        final Schemas previousBundleSchemas = moduleSchemas.put( moduleKey, schemas );
        if ( previousBundleSchemas != null )
        {
            for ( Schema schema : previousBundleSchemas )
            {
                allSchemas.remove( schema.getName() );
            }
        }

        for ( Schema schema : schemas )
        {
            allSchemas.put( schema.getName(), schema );
        }
    }

    private void removeBundleSchemas( final Bundle bundle )
    {
        final ModuleKey moduleKey = ModuleKey.from( bundle );
        final Schemas removedBundleSchemas = moduleSchemas.remove( moduleKey );
        if ( removedBundleSchemas != null )
        {
            for ( Schema schema : removedBundleSchemas )
            {
                allSchemas.remove( schema.getName() );
            }
        }
    }

    @Override
    public Schema getSchema( final SchemaName schemaName )
    {
        return allSchemas.get( schemaName );
    }

    @Override
    public ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final Schema schema = getSchema( contentTypeName );
        return schema != null && schema.getType().isContentType() ? (ContentType) schema : null;
    }

    @Override
    public Mixin getMixin( final MixinName mixinName )
    {
        final Schema schema = getSchema( mixinName );
        return schema != null && schema.getType().isMixin() ? (Mixin) schema : null;
    }

    @Override
    public RelationshipType getRelationshipType( final RelationshipTypeName relationshipTypeName )
    {
        final Schema schema = getSchema( relationshipTypeName );
        return schema != null && schema.getType().isRelationshipType() ? (RelationshipType) schema : null;
    }

    @Override
    public Schemas getAllSchemas()
    {
        return Schemas.from( allSchemas.values() );
    }

    @Override
    public ContentTypes getAllContentTypes()
    {
        final List<ContentType> contentTypes = allSchemas.values().stream().
            filter( ( schema ) -> schema.getType().isContentType() ).
            map( ( schema ) -> (ContentType) schema ).
            collect( toList() );
        return ContentTypes.from( contentTypes );
    }

    @Override
    public Mixins getAllMixins()
    {
        final List<Mixin> mixins = allSchemas.values().stream().
            filter( ( schema ) -> schema.getType().isMixin() ).
            map( ( schema ) -> (Mixin) schema ).
            collect( toList() );
        return Mixins.from( mixins );
    }

    @Override
    public RelationshipTypes getAllRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypes = allSchemas.values().stream().
            filter( ( schema ) -> schema.getType().isRelationshipType() ).
            map( ( schema ) -> (RelationshipType) schema ).
            collect( toList() );
        return RelationshipTypes.from( relationshipTypes );
    }

    @Override
    public Schemas getModuleSchemas( final ModuleKey moduleKey )
    {
        return moduleSchemas.get( moduleKey );
    }
}
