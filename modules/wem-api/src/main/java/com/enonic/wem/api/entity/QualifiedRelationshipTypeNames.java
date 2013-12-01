package com.enonic.wem.api.entity;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class QualifiedRelationshipTypeNames
    extends AbstractImmutableEntitySet<RelationshipTypeName>
{
    private QualifiedRelationshipTypeNames( final ImmutableSet<RelationshipTypeName> set )
    {
        super( set );
    }

    public QualifiedRelationshipTypeNames add( final String... relationshipTypeNames )
    {
        return add( parseQualifiedNames( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames add( final RelationshipTypeName... relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames add( final Iterable<RelationshipTypeName> relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private QualifiedRelationshipTypeNames add( final ImmutableSet<RelationshipTypeName> relationshipTypeNames )
    {
        final HashSet<RelationshipTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( relationshipTypeNames );
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public static QualifiedRelationshipTypeNames empty()
    {
        final ImmutableSet<RelationshipTypeName> set = ImmutableSet.of();
        return new QualifiedRelationshipTypeNames( set );
    }

    public static QualifiedRelationshipTypeNames from( final String... relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( parseQualifiedNames( relationshipTypeNames ) );
    }

    public static QualifiedRelationshipTypeNames from( final RelationshipTypeName... relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public static QualifiedRelationshipTypeNames from( final Iterable<RelationshipTypeName> relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private static ImmutableSet<RelationshipTypeName> parseQualifiedNames( final String... relationshipTypeNames )
    {
        final Collection<String> list = Lists.newArrayList( relationshipTypeNames );
        final Collection<RelationshipTypeName> relationshipTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( relationshipTypeNameList );
    }

    private final static class ParseFunction
        implements Function<String, RelationshipTypeName>
    {
        @Override
        public RelationshipTypeName apply( final String value )
        {
            return RelationshipTypeName.from( value );
        }
    }
}
