package com.enonic.wem.api.content.schema.mixin;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.util.AbstractImmutableEntitySet;

public final class QualifiedMixinNames
    extends AbstractImmutableEntitySet<QualifiedMixinName>
{
    protected QualifiedMixinNames( final ImmutableSet<QualifiedMixinName> set )
    {
        super( set );
    }

    public QualifiedMixinName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
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
