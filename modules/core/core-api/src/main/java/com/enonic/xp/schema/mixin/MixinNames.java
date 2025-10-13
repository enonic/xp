package com.enonic.xp.schema.mixin;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class MixinNames
    extends AbstractImmutableEntitySet<FormFragmentName>
{
    private static final MixinNames EMPTY = new MixinNames( ImmutableSet.of() );

    private MixinNames( final ImmutableSet<FormFragmentName> list )
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
        return mixinNames.stream().map( FormFragmentName::from ).collect( collector() );
    }

    public static MixinNames from( final FormFragmentName... mixinNames )
    {
        return fromInternal( ImmutableSet.copyOf( mixinNames ) );
    }

    public static MixinNames from( final Iterable<FormFragmentName> mixinNames )
    {
        return mixinNames instanceof MixinNames m ? m : fromInternal( ImmutableSet.copyOf( mixinNames ) );
    }

    private static MixinNames fromInternal( final ImmutableSet<FormFragmentName> mixinNames )
    {
        return mixinNames.isEmpty() ? EMPTY : new MixinNames( mixinNames );
    }

    public static Collector<FormFragmentName, ?, MixinNames> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), MixinNames::fromInternal );
    }
}
