package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

import com.enonic.xp.core.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class ValueCountAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final ValueCountAggregationQuery valueCountAggregationQuery )
    {
        return new ValueCountBuilder( valueCountAggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveStringFieldName( valueCountAggregationQuery.getFieldName() ) );
    }

}
