package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;

public final class QualifiedSubTypeNames
    implements Iterable<QualifiedSubTypeName>
{
    private final ImmutableSet<QualifiedSubTypeName> set;

    private QualifiedSubTypeNames( final ImmutableSet<QualifiedSubTypeName> set )
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

    public QualifiedSubTypeName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final QualifiedSubTypeName ref )
    {
        return this.set.contains( ref );
    }

    public Set<QualifiedSubTypeName> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<QualifiedSubTypeName> iterator()
    {
        return this.set.iterator();
    }

    public QualifiedSubTypeNames add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public QualifiedSubTypeNames add( final QualifiedSubTypeName... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public QualifiedSubTypeNames add( final Iterable<QualifiedSubTypeName> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private QualifiedSubTypeNames add( final ImmutableSet<QualifiedSubTypeName> paths )
    {
        final HashSet<QualifiedSubTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new QualifiedSubTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public QualifiedSubTypeNames remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public QualifiedSubTypeNames remove( final QualifiedSubTypeName... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public QualifiedSubTypeNames remove( final Iterable<QualifiedSubTypeName> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private QualifiedSubTypeNames remove( final ImmutableSet<QualifiedSubTypeName> paths )
    {
        final HashSet<QualifiedSubTypeName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new QualifiedSubTypeNames( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof QualifiedSubTypeNames ) && this.set.equals( ( (QualifiedSubTypeNames) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static QualifiedSubTypeNames empty()
    {
        final ImmutableSet<QualifiedSubTypeName> set = ImmutableSet.of();
        return new QualifiedSubTypeNames( set );
    }

    public static QualifiedSubTypeNames from( final String... paths )
    {
        return new QualifiedSubTypeNames( parsePaths( paths ) );
    }

    public static QualifiedSubTypeNames from( final QualifiedSubTypeName... paths )
    {
        return new QualifiedSubTypeNames( ImmutableSet.copyOf( paths ) );
    }

    public static QualifiedSubTypeNames from( final Iterable<QualifiedSubTypeName> paths )
    {
        return new QualifiedSubTypeNames( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<QualifiedSubTypeName> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<QualifiedSubTypeName> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, QualifiedSubTypeName>
    {
        @Override
        public QualifiedSubTypeName apply( final String value )
        {
            return QualifiedSubTypeName.from( value );
        }
    }
}
