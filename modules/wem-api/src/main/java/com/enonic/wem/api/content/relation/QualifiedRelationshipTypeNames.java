package com.enonic.wem.api.content.relation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class QualifiedRelationshipTypeNames
    implements Iterable<QualifiedRelationshipTypeName>
{
    private final ImmutableSet<QualifiedRelationshipTypeName> set;

    private QualifiedRelationshipTypeNames( final ImmutableSet<QualifiedRelationshipTypeName> set )
    {
        this.set = set;
    }

    public int getSize()
    {
        return this.set.size();
    }

    public boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public QualifiedRelationshipTypeName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final QualifiedRelationshipTypeName ref )
    {
        return this.set.contains( ref );
    }

    public Set<QualifiedRelationshipTypeName> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<QualifiedRelationshipTypeName> iterator()
    {
        return this.set.iterator();
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

    public QualifiedRelationshipTypeNames remove( final String... relationshipTypeNames )
    {
        return remove( parseQualifiedNames( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames remove( final QualifiedRelationshipTypeName... relationshipTypeNames )
    {
        return remove( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    public QualifiedRelationshipTypeNames remove( final Iterable<QualifiedRelationshipTypeName> relationshipTypeNames )
    {
        return remove( ImmutableSet.copyOf( relationshipTypeNames ) );
    }

    private QualifiedRelationshipTypeNames remove( final ImmutableSet<QualifiedRelationshipTypeName> relationshipTypeNames )
    {
        final HashSet<QualifiedRelationshipTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( relationshipTypeNames );
        return new QualifiedRelationshipTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof QualifiedRelationshipTypeNames ) && this.set.equals( ( (QualifiedRelationshipTypeNames) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
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
