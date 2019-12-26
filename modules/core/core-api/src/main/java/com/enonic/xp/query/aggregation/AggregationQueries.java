package com.enonic.xp.query.aggregation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class AggregationQueries
    extends AbstractImmutableEntitySet<AggregationQuery>
{
    private AggregationQueries( final ImmutableSet<AggregationQuery> set )
    {
        super( set );
    }

    private AggregationQueries( final Set<AggregationQuery> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static AggregationQueries empty()
    {
        final Set<AggregationQuery> returnFields = new HashSet<>();
        return new AggregationQueries( returnFields );
    }

    public static AggregationQueries fromCollection( final Collection<AggregationQuery> aggregationQueries )
    {
        return new AggregationQueries( ImmutableSet.copyOf( aggregationQueries ) );
    }

    public static final class Builder
    {
        private final Set<AggregationQuery> aggregationQueries = new HashSet<>();

        public Builder add( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public AggregationQueries build()
        {
            return new AggregationQueries( ImmutableSet.copyOf( this.aggregationQueries ) );
        }
    }

}
