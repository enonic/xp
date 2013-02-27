package com.enonic.wem.api.content.schema.relationship;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class QualifiedRelationshipTypeNames
    extends AbstractImmutableEntitySet<QualifiedRelationshipTypeName>
{
    private QualifiedRelationshipTypeNames( final ImmutableSet<QualifiedRelationshipTypeName> set )
    {
        super( set );
    }

    public QualifiedRelationshipTypeNames add( final String... relationshipTypeNames )
    {
        return add( parseQualifiedNames( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames add( final QualifiedRelationshipTypeName... relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames add( final Iterable<QualifiedRelationshipTypeName> relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private QualifiedRelationshipTypeNames add( final ImmutableSet<QualifiedRelationshipTypeName> relationshipTypeNames )
    {
        final HashSet<QualifiedRelationshipTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( relationshipTypeNames );
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public static QualifiedRelationshipTypeNames empty()
    {
        final ImmutableSet<QualifiedRelationshipTypeName> set = ImmutableSet.of();
        return new QualifiedRelationshipTypeNames( set );
    }

    public static QualifiedRelationshipTypeNames from( final String... relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( parseQualifiedNames( relationshipTypeNames ) );
    }

    public static QualifiedRelationshipTypeNames from( final QualifiedRelationshipTypeName... relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public static QualifiedRelationshipTypeNames from( final Iterable<QualifiedRelationshipTypeName> relationshipTypeNames )
    {
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private static ImmutableSet<QualifiedRelationshipTypeName> parseQualifiedNames( final String... relationshipTypeNames )
    {
        final Collection<String> list = Lists.newArrayList( relationshipTypeNames );
        final Collection<QualifiedRelationshipTypeName> relationshipTypeNameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( relationshipTypeNameList );
    }

    private final static class ParseFunction
        implements Function<String, QualifiedRelationshipTypeName>
    {
        @Override
        public QualifiedRelationshipTypeName apply( final String value )
        {
            return QualifiedRelationshipTypeName.from( value );
        }
    }
}
