package com.enonic.wem.api.schema.relationship;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class RelationshipTypeNames
    extends AbstractImmutableEntitySet<RelationshipTypeName>
{
    private RelationshipTypeNames( final ImmutableSet<RelationshipTypeName> set )
    {
        super( set );
    }

    public RelationshipTypeNames add( final String... relationshipTypeNames )
    {
        return add( parseQualifiedNames( relationshipTypeNames ) );
    }

    public RelationshipTypeNames add( final RelationshipTypeName... relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public RelationshipTypeNames add( final Iterable<RelationshipTypeName> relationshipTypeNames )
    {
        return add( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private RelationshipTypeNames add( final ImmutableSet<RelationshipTypeName> relationshipTypeNames )
    {
        final HashSet<RelationshipTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( relationshipTypeNames );
        return new RelationshipTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public static RelationshipTypeNames empty()
    {
        final ImmutableSet<RelationshipTypeName> set = ImmutableSet.of();
        return new RelationshipTypeNames( set );
    }

    public static RelationshipTypeNames from( final String... relationshipTypeNames )
    {
        return new RelationshipTypeNames( parseQualifiedNames( relationshipTypeNames ) );
    }

    public static RelationshipTypeNames from( final RelationshipTypeName... relationshipTypeNames )
    {
        return new RelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public static RelationshipTypeNames from( final Iterable<RelationshipTypeName> relationshipTypeNames )
    {
        return new RelationshipTypeNames( ImmutableSet.copyOf( relationshipTypeNames ) );
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
