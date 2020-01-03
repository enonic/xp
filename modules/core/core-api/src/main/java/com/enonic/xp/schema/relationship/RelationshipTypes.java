package com.enonic.xp.schema.relationship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class RelationshipTypes
    extends AbstractImmutableEntityList<RelationshipType>
{
    private final ImmutableMap<RelationshipTypeName, RelationshipType> map;

    private RelationshipTypes( final ImmutableList<RelationshipType> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( RelationshipType::getName, Function.identity() ) );
    }

    public RelationshipTypes add( final RelationshipType... relationshipTypes )
    {
        return add( ImmutableList.copyOf( relationshipTypes ) );
    }

    public RelationshipTypes add( final Iterable<RelationshipType> relationshipTypes )
    {
        return add( ImmutableList.copyOf( relationshipTypes ) );
    }

    private RelationshipTypes add( final ImmutableList<RelationshipType> relationshipTypes )
    {
        final List<RelationshipType> tmp = new ArrayList<>();
        tmp.addAll( this.list );
        tmp.addAll( relationshipTypes );

        return new RelationshipTypes( ImmutableList.copyOf( tmp ) );
    }

    public Set<RelationshipTypeName> getNames()
    {
        return map.keySet();
    }

    public RelationshipType get( final RelationshipTypeName relationshipTypeName )
    {
        return map.get( relationshipTypeName );
    }

    public RelationshipTypes filter( final Predicate<RelationshipType> filter )
    {
        return from( this.map.values().stream().filter( filter ).iterator() );
    }

    public static RelationshipTypes empty()
    {
        return new RelationshipTypes( ImmutableList.of() );
    }

    public static RelationshipTypes from( final RelationshipType... relationshipTypes )
    {
        return new RelationshipTypes( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static RelationshipTypes from( final Iterable<RelationshipType> relationshipTypes )
    {
        return new RelationshipTypes( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static RelationshipTypes from( final Iterator<RelationshipType> relationshipTypes )
    {
        return new RelationshipTypes( ImmutableList.copyOf( relationshipTypes ) );
    }
}
