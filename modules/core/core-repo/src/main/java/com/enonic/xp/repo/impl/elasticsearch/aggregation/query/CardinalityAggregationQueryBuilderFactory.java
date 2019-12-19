package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;

import com.enonic.xp.query.aggregation.CardinalityAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class CardinalityAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public CardinalityAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final CardinalityAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

        final CardinalityAggregationBuilder cardinalityBuilder =
            new CardinalityAggregationBuilder( aggregationQuery.getName(), ValueType.STRING ).
                field( fieldName );

        return cardinalityBuilder;
    }
}
