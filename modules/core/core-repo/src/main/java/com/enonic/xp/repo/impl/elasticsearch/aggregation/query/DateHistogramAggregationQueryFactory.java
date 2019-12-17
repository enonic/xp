package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
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

    AbstractAggregationBuilder create( final DateHistogramAggregationQuery aggregationQuery )
    {
        final DateHistogramAggregationBuilder builder = AggregationBuilders.dateHistogram( aggregationQuery.getName() ).
            dateHistogramInterval( new DateHistogramInterval( aggregationQuery.getInterval() ) ).
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
