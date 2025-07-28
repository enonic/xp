package com.enonic.xp.query.aggregation;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class AggregationQueries
    extends AbstractImmutableEntityList<AggregationQuery>
{
    private static final AggregationQueries EMPTY = new AggregationQueries( ImmutableList.of() );

    private AggregationQueries( final ImmutableList<AggregationQuery> set )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static AggregationQueries empty()
    {
        return EMPTY;
    }

    public static AggregationQueries from( final Iterable<AggregationQuery> aggregationQueries )
    {
        return aggregationQueries instanceof AggregationQueries a ? a : fromInternal( ImmutableList.copyOf( aggregationQueries ) );
    }

    public static Collector<AggregationQuery, ?, AggregationQueries> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), AggregationQueries::fromInternal );
    }

    private static AggregationQueries fromInternal( final ImmutableList<AggregationQuery> aggregationQueries )
    {
        return aggregationQueries.isEmpty() ? EMPTY : new AggregationQueries( aggregationQueries );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<AggregationQuery> aggregationQueries = ImmutableList.builder();

        public Builder add( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public Builder addAll( final Iterable<? extends AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries );
            return this;
        }

        public AggregationQueries build()
        {
            return fromInternal( this.aggregationQueries.build() );
        }
    }
}
