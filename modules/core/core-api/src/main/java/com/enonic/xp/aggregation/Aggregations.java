package com.enonic.xp.aggregation;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Aggregations
    extends AbstractImmutableEntityList<Aggregation>
{
    private static final Aggregations EMPTY = new Aggregations( ImmutableList.of() );

    private Aggregations( final ImmutableList<Aggregation> list )
    {
        super( list );
    }

    public static Aggregations empty()
    {
        return EMPTY;
    }

    public static Aggregations from( final Iterable<Aggregation> aggregations )
    {
        return aggregations instanceof Aggregations a ? a : fromInternal( ImmutableList.copyOf( aggregations ) );
    }

    public static Aggregations from( final Aggregation... aggregations )
    {
        return fromInternal( ImmutableList.copyOf( aggregations ) );
    }

    public static Collector<Aggregation, ?, Aggregations> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Aggregations::fromInternal );
    }

    public Aggregation get( final String name )
    {
        return this.stream().filter( ( agg ) -> name.equals( agg.getName() ) ).findFirst().orElse( null );
    }

    private static Aggregations fromInternal( final ImmutableList<Aggregation> set )
    {
        return set.isEmpty() ? EMPTY : new Aggregations( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Aggregation> aggregations = ImmutableList.builder();

        public Builder add( final Aggregation aggregation )
        {
            this.aggregations.add( aggregation );
            return this;
        }

        public Builder addAll( final Iterable<Aggregation> aggregations )
        {
            this.aggregations.addAll( aggregations );
            return this;
        }

        public Aggregations build()
        {
            return fromInternal( aggregations.build() );
        }
    }
}
