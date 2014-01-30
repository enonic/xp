package com.enonic.wem.core.index.query.aggregation;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.google.common.collect.Sets;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;

public class AggregationBuilderFactory
{
    public Set<AggregationBuilder> create( final Collection<AggregationQuery> aggregationQueries )
    {

        Set<AggregationBuilder> aggregationBuilders = Sets.newHashSet();

        for ( final AggregationQuery aggregationQuery : aggregationQueries )
        {
            if ( aggregationQuery instanceof TermsAggregationQuery )
            {
                aggregationBuilders.add( createTerms( (TermsAggregationQuery) aggregationQuery ) );
            }
        }

        return aggregationBuilders;

    }

    private AggregationBuilder createTerms( final TermsAggregationQuery aggregation )
    {

        final TermsBuilder termsBuilder = new TermsBuilder( aggregation.getName() ).
            minDocCount( 0 ).
            field( aggregation.getFieldName() ).
            size( aggregation.getSize() );

        return termsBuilder;
    }

}
