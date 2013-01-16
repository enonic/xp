package com.enonic.wem.api.content.relation;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class RelationshipTypes
    extends AbstractImmutableEntityList<RelationshipType>
{
    private final ImmutableMap<QualifiedRelationshipTypeName, RelationshipType> map;

    private RelationshipTypes( final ImmutableList<RelationshipType> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Set<QualifiedRelationshipTypeName> getNames()
    {
        final Collection<QualifiedRelationshipTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public RelationshipType get( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        return map.get( relationshipTypeName );
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

    public static RelationshipTypes from( final Iterable<? extends RelationshipType> relationshipTypes )
    {
        return new RelationshipTypes( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static RelationshipTypes from( final Collection<? extends RelationshipType> relationshipTypes )
    {
        return new RelationshipTypes( ImmutableList.copyOf( relationshipTypes ) );
    }

    private final static class ToNameFunction
        implements Function<RelationshipType, QualifiedRelationshipTypeName>
    {
        @Override
        public QualifiedRelationshipTypeName apply( final RelationshipType value )
        {
            return value.getQualifiedRelationshipTypeName();
        }
    }
}
