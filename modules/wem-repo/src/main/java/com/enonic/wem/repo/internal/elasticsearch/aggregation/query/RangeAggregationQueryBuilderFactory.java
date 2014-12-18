package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;

import com.google.common.base.Strings;

import com.enonic.wem.api.query.aggregation.AbstractRangeAggregationQuery;
import com.enonic.wem.api.query.aggregation.DateRange;
import com.enonic.wem.api.query.aggregation.DateRangeAggregationQuery;
import com.enonic.wem.api.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.wem.api.query.aggregation.NumericRange;
import com.enonic.wem.api.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

class RangeAggregationQueryBuilderFactory
{
    static AggregationBuilder create( final AbstractRangeAggregationQuery aggregationQuery )
    {
        if ( aggregationQuery instanceof DateRangeAggregationQuery )
        {
            return createDateRage( (DateRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof NumericRangeAggregationQuery )
        {
            return createNumericRange( (NumericRangeAggregationQuery) aggregationQuery );
        }
        else if ( aggregationQuery instanceof GeoDistanceAggregationQuery )
        {
            return GeoDistanceAggregationQueryBuilderFactory.create( (GeoDistanceAggregationQuery) aggregationQuery );
        }
        else
        {
            throw new UnsupportedOperationException(
                "Range aggreagations of type  " + aggregationQuery.getClass().getName() + " not implemented" );
        }
    }

    private static AggregationBuilder createDateRage( final DateRangeAggregationQuery query )
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

    private static AggregationBuilder createNumericRange( final NumericRangeAggregationQuery query )
    {
        final String fieldName = IndexQueryFieldNameResolver.resolveNumericFieldName( query.getFieldName() );

        final RangeBuilder rangeBuilder = new RangeBuilder( query.getName() ).
            field( fieldName );

        for ( final NumericRange range : query.getRanges() )
        {
            if ( range.getFrom() == null )
            {
                rangeBuilder.addUnboundedTo( range.getKey(), range.getTo() );
            }
            else if ( range.getTo() == null )
            {
                rangeBuilder.addUnboundedFrom( range.getKey(), range.getFrom() );
            }
            else
            {
                rangeBuilder.addRange( range.getKey(), range.getFrom(), range.getTo() );
            }

            if ( Strings.isNullOrEmpty( range.getKey() ) )
            {
                rangeBuilder.format( range.getKey() );
            }
        }

        return rangeBuilder;
    }

}
