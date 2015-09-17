package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;

import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;

class DateHistogramAggregationQueryFactory
{
    static AggregationBuilder create( final DateHistogramAggregationQuery aggregationQuery )
    {
        final DateHistogramBuilder builder = new DateHistogramBuilder( aggregationQuery.getName() ).
            interval( new DateHistogram.Interval( aggregationQuery.getInterval() ) ).
            field( IndexQueryFieldNameResolver.resolveDateTimeFieldName( aggregationQuery.getFieldName() ) );

        if ( aggregationQuery.getFormat() != null )
        {
            builder.format( aggregationQuery.getFormat() );
        }

        if ( aggregationQuery.getMinDocCount() != null )
        {
            builder.minDocCount( aggregationQuery.getMinDocCount() );
        }

        return builder;
    }
}
