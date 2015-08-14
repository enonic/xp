package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;

class TermsAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final TermsAggregationQuery aggregationQuery )
    {
        final String fieldName = IndexQueryFieldNameResolver.resolveStringFieldName( aggregationQuery.getFieldName() );

        final TermsBuilder termsBuilder = new TermsBuilder( aggregationQuery.getName() ).
            minDocCount( aggregationQuery.getMinDocCount() ).
            field( fieldName ).
            size( aggregationQuery.getSize() );

        if ( aggregationQuery.getOrderType() == TermsAggregationQuery.Type.TERM )
        {
            termsBuilder.order( Terms.Order.term( aggregationQuery.getOrderDirection().equals( TermsAggregationQuery.Direction.ASC ) ) );
        }
        else
        {
            termsBuilder.order( Terms.Order.count( aggregationQuery.getOrderDirection().equals( TermsAggregationQuery.Direction.ASC ) ) );
        }

        return termsBuilder;
    }
}
