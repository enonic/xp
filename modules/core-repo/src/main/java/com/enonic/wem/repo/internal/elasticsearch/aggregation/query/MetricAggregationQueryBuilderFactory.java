package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import com.enonic.xp.core.query.aggregation.MetricAggregationQuery;
import com.enonic.xp.core.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.core.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.core.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.core.query.aggregation.metric.ValueCountAggregationQuery;

class MetricAggregationQueryBuilderFactory
{
    public static AbstractAggregationBuilder create( final MetricAggregationQuery metricAggregationQuery )
    {
        if ( metricAggregationQuery instanceof StatsAggregationQuery )
        {
            return StatsAggregationQueryBuilderFactory.create( (StatsAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof ValueCountAggregationQuery )
        {
            return ValueCountAggregationQueryBuilderFactory.create( (ValueCountAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof MinAggregationQuery )
        {
            return MinAggregationQueryBuilderFactory.create( (MinAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof MaxAggregationQuery )
        {
            return MaxAggregationQueryBuilderFactory.create( (MaxAggregationQuery) metricAggregationQuery );
        }
        else
        {
            throw new IllegalArgumentException( "Unexpected aggregation type: " + metricAggregationQuery.getClass() );
        }
    }

}
