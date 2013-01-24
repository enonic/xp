package com.enonic.wem.api.content.space;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class Spaces
    extends AbstractImmutableEntityList<Space>
{
    private final ImmutableMap<SpaceName, Space> map;

    private Spaces( final ImmutableList<Space> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<SpaceName> getNames()
    {
        return map.keySet();
    }

    public Space getSpace( final SpaceName spaceName )
    {
        return map.get( spaceName );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Spaces empty()
    {
        final ImmutableList<Space> list = ImmutableList.of();
        return new Spaces( list );
    }

    public static Spaces from( final Space... spaces )
    {
        return new Spaces( ImmutableList.copyOf( spaces ) );
    }

    public static Spaces from( final Iterable<? extends Space> spaces )
    {
        return new Spaces( ImmutableList.copyOf( spaces ) );
    }

    public static Spaces from( final Collection<? extends Space> spaces )
    {
        return new Spaces( ImmutableList.copyOf( spaces ) );
    }

    private final static class ToNameFunction
        implements Function<Space, SpaceName>
    {
        @Override
        public SpaceName apply( final Space value )
        {
            return value.getName();
        }
    }

}
