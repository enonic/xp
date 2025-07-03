package com.enonic.xp.schema.mixin;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MixinNames
    extends AbstractImmutableEntityList<MixinName>
{
    private static final MixinNames EMPTY = new MixinNames( ImmutableList.of() );

    private MixinNames( final ImmutableList<MixinName> list )
    {
        super( list );
    }

    public static MixinNames empty()
    {
        return EMPTY;
    }

    public static MixinNames from( final String... mixinNames )
    {
        return from(  Arrays.asList( mixinNames ) );
    }

    public static MixinNames from( final Collection<String> mixinNames )
    {
        return mixinNames.stream().map( MixinName::from ).collect( collecting() );
    }

    public static MixinNames from( final MixinName... mixinNames )
    {
        return fromInternal( ImmutableList.copyOf( mixinNames ) );
    }

    public static MixinNames from( final Iterable<MixinName> mixinNames )
    {
        return fromInternal( ImmutableList.copyOf( mixinNames ) );
    }

    private static MixinNames fromInternal( final ImmutableList<MixinName> mixinNames )
    {
        return mixinNames.isEmpty() ? EMPTY : new MixinNames( mixinNames );
    }

    public static Collector<MixinName, ?, MixinNames> collecting()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MixinNames::fromInternal );
    }
}
