package com.enonic.xp.schema.relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class RelationshipTypes
    extends AbstractImmutableEntityList<RelationshipType>
{
    private final ImmutableMap<RelationshipTypeName, RelationshipType> map;

    private RelationshipTypes( final ImmutableList<RelationshipType> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
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
        final Collection<RelationshipTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
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
        final ImmutableList<RelationshipType> list = ImmutableList.of();
        return new RelationshipTypes( list );
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

    private final static class ToNameFunction
        implements Function<RelationshipType, RelationshipTypeName>
    {
        @Override
        public RelationshipTypeName apply( final RelationshipType value )
        {
            return value.getName();
        }
    }
}
