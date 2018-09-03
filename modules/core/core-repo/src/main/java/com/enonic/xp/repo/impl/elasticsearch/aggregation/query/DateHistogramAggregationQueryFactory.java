package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class DateHistogramAggregationQueryFactory
    extends AbstractBuilderFactory
{
    public DateHistogramAggregationQueryFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final DateHistogramAggregationQuery aggregationQuery )
    {
        final DateHistogramBuilder builder = new DateHistogramBuilder( aggregationQuery.getName() ).
            interval( new DateHistogramInterval( aggregationQuery.getInterval() ) ).
            field( fieldNameResolver.resolve( aggregationQuery.getFieldName(), IndexValueType.DATETIME ) );

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
