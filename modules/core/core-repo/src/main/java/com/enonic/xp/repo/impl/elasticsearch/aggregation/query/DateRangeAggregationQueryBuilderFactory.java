package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexValueType;

class DateRangeAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public DateRangeAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final DateRangeAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.DATETIME );

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
