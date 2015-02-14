package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;

import com.enonic.xp.core.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class StatsAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final StatsAggregationQuery statsAggregationQuery )
    {
        return new StatsBuilder( statsAggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( statsAggregationQuery.getFieldName() ) );
    }


}
