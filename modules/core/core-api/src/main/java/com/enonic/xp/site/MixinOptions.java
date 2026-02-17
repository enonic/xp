package com.enonic.xp.site;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MixinOptions
    extends AbstractImmutableEntityList<MixinOption>
{
    private static final MixinOptions EMPTY = new MixinOptions( ImmutableList.of() );

    private MixinOptions( final ImmutableList<MixinOption> list )
    {
        super( list );
    }

    public static MixinOptions empty()
    {
        return EMPTY;
    }

    public static Collector<MixinOption, ?, MixinOptions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MixinOptions::fromInternal );
    }

    private static MixinOptions fromInternal( final ImmutableList<MixinOption> list )
    {
        return list.isEmpty() ? EMPTY : new MixinOptions( list );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private final ImmutableList.Builder<MixinOption> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( MixinOption mixinOption )
        {
            builder.add( mixinOption );
            return this;
        }

        public Builder addAll( Iterable<MixinOption> mixinOptions )
        {
            builder.addAll( mixinOptions );
            return this;
        }

        public MixinOptions build()
        {
            return fromInternal( builder.build() );
        }
    }

}
