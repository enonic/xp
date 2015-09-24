package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;

import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

class StatsAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final StatsAggregationQuery statsAggregationQuery )
    {
        return new StatsBuilder( statsAggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( statsAggregationQuery.getFieldName() ) );
    }


}
