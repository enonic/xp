package com.enonic.xp.query.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class StatusesAggregationQuery
    extends BucketAggregationQuery
{
    public StatusesAggregationQuery( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends BucketAggregationQuery.Builder<Builder>
    {
        public Builder( final String name )
        {
            super( name );
        }

        public StatusesAggregationQuery build()
        {
            return new StatusesAggregationQuery( this );
        }
    }
}
