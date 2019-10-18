package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
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
        return AggregationBuilders.max( aggregationQuery.getName() ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }

}
