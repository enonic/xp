package com.enonic.xp.query.aggregation;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class AggregationQueries
    extends AbstractImmutableEntitySet<AggregationQuery>
{
    private AggregationQueries( final ImmutableSet<AggregationQuery> set )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static AggregationQueries empty()
    {
        return new AggregationQueries( ImmutableSet.of() );
    }

    public static AggregationQueries fromCollection( final Collection<AggregationQuery> aggregationQueries )
    {
        return new AggregationQueries( ImmutableSet.copyOf( aggregationQueries ) );
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<AggregationQuery> aggregationQueries = ImmutableSet.builder();

        public Builder add( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public AggregationQueries build()
        {
            return new AggregationQueries( this.aggregationQueries.build() );
        }
    }

}
