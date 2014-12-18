package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import java.util.Set;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.google.common.collect.Sets;

import com.enonic.wem.api.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.AbstractRangeAggregationQuery;
import com.enonic.wem.api.query.aggregation.AggregationQueries;
import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.BucketAggregationQuery;
import com.enonic.wem.api.query.aggregation.StatsAggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;

public class AggregationQueryBuilderFactory
{
    public static Set<AbstractAggregationBuilder> create( final AggregationQueries aggregationQueries )
    {
        return doCreate( aggregationQueries );
    }

    private static Set<AbstractAggregationBuilder> doCreate( final AggregationQueries aggregationQueries )
    {
        Set<AbstractAggregationBuilder> aggregationBuilders = Sets.newHashSet();

        for ( final AggregationQuery aggregationQuery : aggregationQueries )
        {
            final AbstractAggregationBuilder aggregationBuilder;

            if ( aggregationQuery instanceof TermsAggregationQuery )
            {
                aggregationBuilder = TermsAggregationQueryBuilderFactory.create( (TermsAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof AbstractRangeAggregationQuery )
            {
                aggregationBuilder = RangeAggregationQueryBuilderFactory.create( (AbstractRangeAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof AbstractHistogramAggregationQuery )
            {
                aggregationBuilder = HistogramAggregationQueryBuilderFactory.create( (AbstractHistogramAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof StatsAggregationQuery )
            {
                aggregationBuilder = StatsAggregationQueryBuilderFactory.create( (StatsAggregationQuery) aggregationQuery );
            }
            else
            {
                throw new IllegalArgumentException( "Unexpected aggregation type: " + aggregationQuery.getClass() );
            }

            handleSubAggregations( aggregationQuery, aggregationBuilder );

            aggregationBuilders.add( aggregationBuilder );
        }

        return aggregationBuilders;
    }

    private static void handleSubAggregations( final AggregationQuery aggregationQuery,
                                               final AbstractAggregationBuilder aggregationBuilder )
    {
        if ( aggregationQuery instanceof BucketAggregationQuery && aggregationBuilder instanceof AggregationBuilder )
        {
            final Set<AbstractAggregationBuilder> subAggregations =
                doCreate( ( (BucketAggregationQuery) aggregationQuery ).getSubQueries() );

            subAggregations.forEach( ( (AggregationBuilder) aggregationBuilder )::subAggregation );
        }
    }


}
