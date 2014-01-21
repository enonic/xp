package com.enonic.wem.core.index.query.aggregation;


import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.query.aggregation.Aggregations;
import com.enonic.wem.api.query.aggregation.TermsAggregation;

public class AggregationsFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( AggregationsFactory.class );

    public Aggregations create( final org.elasticsearch.search.aggregations.Aggregations aggregations )
    {
        com.enonic.wem.api.query.aggregation.Aggregations.Builder aggregationsBuilder =
            new com.enonic.wem.api.query.aggregation.Aggregations.Builder();

        for ( final org.elasticsearch.search.aggregations.Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof Terms )
            {
                aggregationsBuilder.add( createTermsAggregation( (Terms) aggregation ) );
            }
            else
            {
                LOG.warn( "Aggregation translator for " + aggregation.getClass().getName() + " not implemented, skipping" );
            }
        }

        return aggregationsBuilder.build();
    }

    private TermsAggregation createTermsAggregation( final Terms termsAggregation )
    {
        return TermsAggregation.terms().
            name( termsAggregation.getName() ).
            buckets( BucketsFactory.create( termsAggregation.buckets() ) ).
            build();
    }
}


