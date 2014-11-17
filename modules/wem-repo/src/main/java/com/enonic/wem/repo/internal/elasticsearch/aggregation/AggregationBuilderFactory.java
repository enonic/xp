package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.google.common.collect.Sets;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.RangeAggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

public class AggregationBuilderFactory
{
    public static Set<AggregationBuilder> create( final Collection<AggregationQuery> aggregationQueries )
    {

        Set<AggregationBuilder> aggregationBuilders = Sets.newHashSet();

        for ( final AggregationQuery aggregationQuery : aggregationQueries )
        {
            if ( aggregationQuery instanceof TermsAggregationQuery )
            {
                aggregationBuilders.add( createTerms( (TermsAggregationQuery) aggregationQuery ) );
            }
            else if ( aggregationQuery instanceof RangeAggregationQuery )
            {
                aggregationBuilders.add( RangeAggregationBuilderFactory.create( (RangeAggregationQuery) aggregationQuery ) );
            }
        }

        return aggregationBuilders;
    }

    private static AggregationBuilder createTerms( final TermsAggregationQuery aggregationQuery )
    {
        final String fieldName = IndexQueryFieldNameResolver.resolveStringFieldName( aggregationQuery.getFieldName() );

        final TermsBuilder termsBuilder = new TermsBuilder( aggregationQuery.getName() ).
            minDocCount( 0 ).
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
