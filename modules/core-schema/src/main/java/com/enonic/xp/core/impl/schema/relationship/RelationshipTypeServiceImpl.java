package com.enonic.xp.core.impl.schema.relationship;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeProvider;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;

@Component(immediate = true)
public final class RelationshipTypeServiceImpl
    implements RelationshipTypeService
{
    private final Map<RelationshipTypeName, RelationshipType> map;

    public RelationshipTypeServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public RelationshipType getByName( final RelationshipTypeName name )
    {
        return this.map.get( name );
    }

    @Override
    public RelationshipTypes getAll()
    {
        return RelationshipTypes.from( this.map.values() );
    }

    @Override
    public RelationshipTypes getByModule( final ModuleKey moduleKey )
    {
        final Stream<RelationshipType> stream = this.map.values().stream().filter( new Predicate<RelationshipType>()
        {
            @Override
            public boolean test( final RelationshipType value )
            {
                return value.getName().getModuleKey().equals( moduleKey );
            }
        } );

        return RelationshipTypes.from( stream.collect( Collectors.toList() ) );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addProvider( final RelationshipTypeProvider provider )
    {
        for ( final RelationshipType value : provider.get() )
        {
            this.map.put( value.getName(), value );
        }
    }

    public void removeProvider( final RelationshipTypeProvider provider )
    {
        for ( final RelationshipType value : provider.get() )
        {
            this.map.remove( value.getName() );
        }
    }
}
