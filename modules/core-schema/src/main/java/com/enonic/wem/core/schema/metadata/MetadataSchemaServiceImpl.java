package com.enonic.wem.core.schema.metadata;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaProvider;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

@Component(immediate = true)
public final class MetadataSchemaServiceImpl
    implements MetadataSchemaService
{
    private final Map<MetadataSchemaName, MetadataSchema> map;

    public MetadataSchemaServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public MetadataSchema getByName( final MetadataSchemaName name )
    {
        return this.map.get( name );
    }

    @Override
    public MetadataSchemas getAll()
    {
        return MetadataSchemas.from( this.map.values() );
    }

    @Override
    public MetadataSchemas getByModule( final ModuleKey moduleKey )
    {
        final Stream<MetadataSchema> stream = this.map.values().stream().filter( new Predicate<MetadataSchema>()
        {
            @Override
            public boolean test( final MetadataSchema value )
            {
                return value.getName().getModuleKey().equals( moduleKey );
            }
        } );

        return MetadataSchemas.from( stream.collect( Collectors.toList() ) );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addProvider( final MetadataSchemaProvider provider )
    {
        for ( final MetadataSchema value : provider.get() )
        {
            this.map.put( value.getName(), value );
        }
    }

    public void removeProvider( final MetadataSchemaProvider provider )
    {
        for ( final MetadataSchema value : provider.get() )
        {
            this.map.remove( value.getName() );
        }
    }
}
