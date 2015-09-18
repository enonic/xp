package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

class NumericRangeAggregationQueryFactory
{

    static AggregationBuilder create( final NumericRangeAggregationQuery query )
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
