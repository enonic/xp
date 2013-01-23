package com.enonic.wem.api.content.space;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.util.AbstractImmutableEntitySet;

public final class SpaceNames
    extends AbstractImmutableEntitySet<SpaceName>
{
    private SpaceNames( final ImmutableSet<SpaceName> set )
    {
        super( set );
    }

    @Override
    public int hashCode()
    {
        return this.set.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof SpaceNames ) && this.set.equals( ( (SpaceNames) o ).set );
    }

    @Override
    public String toString()
    {
        return this.set.toString();
    }

    public static SpaceNames empty()
    {
        final ImmutableSet<SpaceName> set = ImmutableSet.of();
        return new SpaceNames( set );
    }

    public static SpaceNames from( final SpaceName... names )
    {
        return new SpaceNames( ImmutableSet.copyOf( names ) );
    }

    public static SpaceNames from( final Iterable<SpaceName> names )
    {
        return new SpaceNames( ImmutableSet.copyOf( names ) );
    }

    public static SpaceNames from( final String... names )
    {
        return new SpaceNames( parseNames( names ) );
    }

    private static ImmutableSet<SpaceName> parseNames( final String... names )
    {
        final Collection<String> list = Lists.newArrayList( names );
        final Collection<SpaceName> nameList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( nameList );
    }

    private final static class ParseFunction
        implements Function<String, SpaceName>
    {
        @Override
        public SpaceName apply( final String value )
        {
            return SpaceName.from( value );
        }
    }
}
