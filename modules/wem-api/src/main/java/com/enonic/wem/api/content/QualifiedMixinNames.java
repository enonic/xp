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

import com.enonic.wem.api.content.type.form.QualifiedMixinName;

public final class QualifiedMixinNames
    implements Iterable<QualifiedMixinName>
{
    private final ImmutableSet<QualifiedMixinName> set;

    private QualifiedMixinNames( final ImmutableSet<QualifiedMixinName> set )
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

    public QualifiedMixinName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final QualifiedMixinName ref )
    {
        return this.set.contains( ref );
    }

    public Set<QualifiedMixinName> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<QualifiedMixinName> iterator()
    {
        return this.set.iterator();
    }

    public QualifiedMixinNames add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public QualifiedMixinNames add( final QualifiedMixinName... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public QualifiedMixinNames add( final Iterable<QualifiedMixinName> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private QualifiedMixinNames add( final ImmutableSet<QualifiedMixinName> paths )
    {
        final HashSet<QualifiedMixinName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new QualifiedMixinNames( ImmutableSet.copyOf( tmp ) );
    }

    public QualifiedMixinNames remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public QualifiedMixinNames remove( final QualifiedMixinName... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public QualifiedMixinNames remove( final Iterable<QualifiedMixinName> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private QualifiedMixinNames remove( final ImmutableSet<QualifiedMixinName> paths )
    {
        final HashSet<QualifiedMixinName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new QualifiedMixinNames( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof QualifiedMixinNames ) && this.set.equals( ( (QualifiedMixinNames) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static QualifiedMixinNames empty()
    {
        final ImmutableSet<QualifiedMixinName> set = ImmutableSet.of();
        return new QualifiedMixinNames( set );
    }

    public static QualifiedMixinNames from( final String... paths )
    {
        return new QualifiedMixinNames( parsePaths( paths ) );
    }

    public static QualifiedMixinNames from( final QualifiedMixinName... paths )
    {
        return new QualifiedMixinNames( ImmutableSet.copyOf( paths ) );
    }

    public static QualifiedMixinNames from( final Iterable<QualifiedMixinName> paths )
    {
        return new QualifiedMixinNames( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<QualifiedMixinName> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<QualifiedMixinName> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, QualifiedMixinName>
    {
        @Override
        public QualifiedMixinName apply( final String value )
        {
            return QualifiedMixinName.from( value );
        }
    }
}
