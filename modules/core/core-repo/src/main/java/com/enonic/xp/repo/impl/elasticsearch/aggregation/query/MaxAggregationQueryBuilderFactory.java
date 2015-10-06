package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;

import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexValueType;

class MaxAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{

    public MaxAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final MaxAggregationQuery aggregationQuery )
    {
        return new MaxBuilder( aggregationQuery.getName() ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }

}
