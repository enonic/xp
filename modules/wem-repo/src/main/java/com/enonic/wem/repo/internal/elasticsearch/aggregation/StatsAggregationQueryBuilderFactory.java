package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;

import com.enonic.wem.api.query.aggregation.StatsAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class StatsAggregationQueryBuilderFactory
{

    public static AbstractAggregationBuilder create( final StatsAggregationQuery statsAggregationQuery )
    {
        return new StatsBuilder( statsAggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( statsAggregationQuery.getFieldName() ) );
    }


}
