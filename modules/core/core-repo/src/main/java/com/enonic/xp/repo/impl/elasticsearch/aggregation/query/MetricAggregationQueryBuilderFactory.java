package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import com.enonic.xp.query.aggregation.MetricAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;

class MetricAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public MetricAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    public AbstractAggregationBuilder create( final MetricAggregationQuery metricAggregationQuery )
    {
        if ( metricAggregationQuery instanceof StatsAggregationQuery )
        {
            return new StatsAggregationQueryBuilderFactory( fieldNameResolver ).create( (StatsAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof ValueCountAggregationQuery )
        {
            return new ValueCountAggregationQueryBuilderFactory( fieldNameResolver ).create(
                (ValueCountAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof MinAggregationQuery )
        {
            return new MinAggregationQueryBuilderFactory( fieldNameResolver ).create( (MinAggregationQuery) metricAggregationQuery );
        }
        else if ( metricAggregationQuery instanceof MaxAggregationQuery )
        {
            return new MaxAggregationQueryBuilderFactory( fieldNameResolver ).create( (MaxAggregationQuery) metricAggregationQuery );
        }
        else
        {
            throw new IllegalArgumentException( "Unexpected aggregation type: " + metricAggregationQuery.getClass() );
        }
    }

}
