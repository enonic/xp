package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class DateRangeAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final DateRangeAggregationQuery query )
    {
        final String fieldName = IndexQueryFieldNameResolver.resolveDateTimeFieldName( query.getFieldName() );

        final DateRangeBuilder dateRangeBuilder = new DateRangeBuilder( query.getName() ).
            field( fieldName );

        if ( !Strings.isNullOrEmpty( query.getFormat() ) )
        {
            dateRangeBuilder.format( query.getFormat() );
        }

        for ( final DateRange dateRange : query.getRanges() )
        {
            dateRangeBuilder.addRange( dateRange.getKey(), dateRange.getFrom(), dateRange.getTo() );
        }

        return dateRangeBuilder;
    }

}
