package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import java.util.Set;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import com.google.common.collect.Sets;

import com.enonic.xp.query.aggregation.AbstractHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.AbstractRangeAggregationQuery;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.BucketAggregationQuery;
import com.enonic.xp.query.aggregation.MetricAggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;

public class AggregationQueryBuilderFactory
{
    private final QueryFieldNameResolver fieldNameResolver;

    public AggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        this.fieldNameResolver = fieldNameResolver;
    }

    public Set<AbstractAggregationBuilder> create( final AggregationQueries aggregationQueries )
    {
        return doCreate( aggregationQueries );
    }

    private Set<AbstractAggregationBuilder> doCreate( final AggregationQueries aggregationQueries )
    {
        Set<AbstractAggregationBuilder> aggregationBuilders = Sets.newHashSet();

        for ( final AggregationQuery aggregationQuery : aggregationQueries )
        {
            final AbstractAggregationBuilder aggregationBuilder;

            if ( aggregationQuery instanceof TermsAggregationQuery )
            {
                aggregationBuilder =
                    new TermsAggregationQueryBuilderFactory( fieldNameResolver ).create( (TermsAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof AbstractRangeAggregationQuery )
            {
                aggregationBuilder =
                    new RangeAggregationQueryBuilderFactory( fieldNameResolver ).create( (AbstractRangeAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof AbstractHistogramAggregationQuery )
            {
                aggregationBuilder = new HistogramAggregationQueryBuilderFactory( fieldNameResolver ).create(
                    (AbstractHistogramAggregationQuery) aggregationQuery );
            }
            else if ( aggregationQuery instanceof MetricAggregationQuery )
            {
                aggregationBuilder =
                    new MetricAggregationQueryBuilderFactory( fieldNameResolver ).create( (MetricAggregationQuery) aggregationQuery );
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

    private void handleSubAggregations( final AggregationQuery aggregationQuery, final AbstractAggregationBuilder aggregationBuilder )
    {
        if ( aggregationQuery instanceof BucketAggregationQuery && aggregationBuilder instanceof AggregationBuilder )
        {
            final Set<AbstractAggregationBuilder> subAggregations =
                doCreate( ( (BucketAggregationQuery) aggregationQuery ).getSubQueries() );

            subAggregations.forEach( ( (AggregationBuilder) aggregationBuilder )::subAggregation );
        }
    }


}
