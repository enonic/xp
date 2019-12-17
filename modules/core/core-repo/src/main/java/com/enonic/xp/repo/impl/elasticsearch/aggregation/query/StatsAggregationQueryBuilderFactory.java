package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class StatsAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public StatsAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final StatsAggregationQuery statsAggregationQuery )
    {
        return AggregationBuilders.stats( statsAggregationQuery.getName() ).
            field( fieldNameResolver.resolve( statsAggregationQuery.getFieldName(), IndexValueType.NUMBER ) );
    }


}
