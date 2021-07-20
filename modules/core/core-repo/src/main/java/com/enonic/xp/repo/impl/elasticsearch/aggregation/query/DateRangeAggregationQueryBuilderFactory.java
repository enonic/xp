package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;

import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static com.google.common.base.Strings.isNullOrEmpty;

class DateRangeAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    DateRangeAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final DateRangeAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.DATETIME );

        final DateRangeBuilder dateRangeBuilder = new DateRangeBuilder( query.getName() ).
            field( fieldName );

        if ( !isNullOrEmpty( query.getFormat() ) )
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
