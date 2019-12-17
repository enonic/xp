package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class ValueCountAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public ValueCountAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final ValueCountAggregationQuery valueCountAggregationQuery )
    {
        return AggregationBuilders.count( valueCountAggregationQuery.getName() ).
            field( fieldNameResolver.resolve( valueCountAggregationQuery.getFieldName(), IndexValueType.STRING ) );
    }

}
