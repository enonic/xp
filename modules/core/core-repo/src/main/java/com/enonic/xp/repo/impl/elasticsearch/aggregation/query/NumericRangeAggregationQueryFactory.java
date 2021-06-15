package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;

import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static com.google.common.base.Strings.isNullOrEmpty;

class NumericRangeAggregationQueryFactory
    extends AbstractBuilderFactory
{
    NumericRangeAggregationQueryFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final NumericRangeAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.NUMBER );

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

            if ( isNullOrEmpty( range.getKey() ) )
            {
                rangeBuilder.format( range.getKey() );
            }
        }

        return rangeBuilder;
    }

}
