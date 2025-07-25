package com.enonic.xp.aggregation;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Buckets
    extends AbstractImmutableEntityList<Bucket>
{
    private Buckets( final ImmutableList<Bucket> list )
    {
        super( list );
    }

    public static Collector<Bucket, ?, Buckets> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Buckets::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Bucket> buckets = ImmutableList.builder();

        public Builder add( final Bucket bucket )
        {
            this.buckets.add( bucket );
            return this;
        }

        public Buckets build()
        {
            return new Buckets( buckets.build() );
        }
    }
}
