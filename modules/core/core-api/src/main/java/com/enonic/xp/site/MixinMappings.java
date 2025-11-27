package com.enonic.xp.site;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MixinMappings
    extends AbstractImmutableEntityList<MixinMapping>
{
    private static final MixinMappings EMPTY = new MixinMappings( ImmutableList.of() );

    private MixinMappings( final ImmutableList<MixinMapping> list )
    {
        super( list );
    }

    public static MixinMappings empty()
    {
        return EMPTY;
    }

    public static MixinMappings from( final MixinMapping... mixinMappings )
    {
        return fromInternal( ImmutableList.copyOf( mixinMappings ) );
    }

    public static MixinMappings from( final Iterable<MixinMapping> mixinMappings )
    {
        return mixinMappings instanceof MixinMappings x ? x : fromInternal( ImmutableList.copyOf( mixinMappings ) );
    }

    private static MixinMappings fromInternal( final ImmutableList<MixinMapping> mixinMappings )
    {
        return mixinMappings.isEmpty() ? EMPTY : new MixinMappings( mixinMappings );
    }

    public static Collector<MixinMapping, ?, MixinMappings> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MixinMappings::fromInternal );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MixinNames getNames()
    {
        return this.stream().map( MixinMapping::getMixinName ).collect( MixinNames.collector() );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<MixinMapping> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public MixinMappings.Builder add( final MixinMapping mixinMapping )
        {
            builder.add( mixinMapping );
            return this;
        }

        public MixinMappings.Builder addAll( final Iterable<MixinMapping> mixinMappings )
        {
            builder.addAll( mixinMappings );
            return this;
        }

        public MixinMappings build()
        {
            return fromInternal( builder.build() );
        }
    }
}
