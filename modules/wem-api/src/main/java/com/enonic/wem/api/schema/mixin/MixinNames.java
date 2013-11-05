package com.enonic.wem.api.schema.mixin;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class MixinNames
    extends AbstractImmutableEntitySet<MixinName>
{
    protected MixinNames( final ImmutableSet<MixinName> set )
    {
        super( set );
    }

    public MixinName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public MixinNames add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public MixinNames add( final MixinName... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public MixinNames add( final Iterable<MixinName> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private MixinNames add( final ImmutableSet<MixinName> paths )
    {
        final HashSet<MixinName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new MixinNames( ImmutableSet.copyOf( tmp ) );
    }

    public MixinNames remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public MixinNames remove( final MixinName... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public MixinNames remove( final Iterable<MixinName> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private MixinNames remove( final ImmutableSet<MixinName> paths )
    {
        final HashSet<MixinName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new MixinNames( ImmutableSet.copyOf( tmp ) );
    }

    public static MixinNames empty()
    {
        final ImmutableSet<MixinName> set = ImmutableSet.of();
        return new MixinNames( set );
    }

    public static MixinNames from( final String... paths )
    {
        return new MixinNames( parsePaths( paths ) );
    }

    public static MixinNames from( final MixinName... paths )
    {
        return new MixinNames( ImmutableSet.copyOf( paths ) );
    }

    public static MixinNames from( final Iterable<MixinName> paths )
    {
        return new MixinNames( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<MixinName> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<MixinName> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, MixinName>
    {
        @Override
        public MixinName apply( final String value )
        {
            return MixinName.from( value );
        }
    }
}
