package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;

import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class TermsAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public TermsAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final TermsAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

        int size = aggregationQuery.getSize();
        if ( size == 0 )
        {
            size = Integer.MAX_VALUE; //mimic deprecated ES2.4 behaviour
        }
        final TermsAggregationBuilder termsBuilder = new TermsAggregationBuilder( aggregationQuery.getName(), ValueType.STRING ).
            minDocCount( aggregationQuery.getMinDocCount() ).
            field( fieldName ).
            size( size );

        final boolean ascendingDirection = aggregationQuery.getOrderDirection().equals( TermsAggregationQuery.Direction.ASC );

        termsBuilder.order( aggregationQuery.getOrderType() == TermsAggregationQuery.Type.TERM
                                ? BucketOrder.key( ascendingDirection )
                                : BucketOrder.count( ascendingDirection ) );

        return termsBuilder;
    }
}
