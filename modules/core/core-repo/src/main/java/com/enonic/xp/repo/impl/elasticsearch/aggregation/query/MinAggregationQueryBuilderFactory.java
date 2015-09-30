package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;

import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexValueType;

class MinAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public MinAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final MinAggregationQuery aggregationQuery )
    {
        return new MinBuilder( aggregationQuery.getName() ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }
}
