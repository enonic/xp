package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;

import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class NumericHistogramAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{

    public NumericHistogramAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final HistogramAggregationQuery aggregationQuery )
    {
        final HistogramAggregationBuilder builder = AggregationBuilders.histogram( aggregationQuery.getName() ).
            interval( aggregationQuery.getInterval() ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.NUMBER ) );

        if ( aggregationQuery.getMinDocCount() != null )
        {
            builder.minDocCount( aggregationQuery.getMinDocCount() );
        }

        if ( aggregationQuery.setExtendedBounds() )
        {
            builder.extendedBounds( aggregationQuery.getExtendedBoundMin(), aggregationQuery.getExtendedBoundMax() );
        }

        if ( aggregationQuery.getOrder() != null )
        {
            builder.order( translate( aggregationQuery.getOrder() ) );
        }

        return builder;
    }

    private BucketOrder translate( HistogramAggregationQuery.Order order )
    {
        switch ( order )
        {
            case KEY_ASC:
                return BucketOrder.key( true );
            case KEY_DESC:
                return BucketOrder.key( false );
            case COUNT_DESC:
                return BucketOrder.count( false );
            case COUNT_ASC:
                return BucketOrder.count( true );
        }

        throw new IllegalArgumentException( "Unknown order value for histogram: " + order.name() );
    }
}
