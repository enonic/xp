package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexValueType;

class TermsAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public TermsAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final TermsAggregationQuery aggregationQuery )
    {
        final String fieldName = fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.STRING );

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
