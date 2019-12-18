package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;

import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static com.google.common.base.Strings.isNullOrEmpty;

class DateRangeAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public DateRangeAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final DateRangeAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.DATETIME );

        final DateRangeAggregationBuilder dateRangeBuilder = new DateRangeAggregationBuilder( query.getName() ).
            field( fieldName );

        if ( !isNullOrEmpty( query.getFormat() ) )
        {
            dateRangeBuilder.format( query.getFormat() );
        }

        for ( final DateRange dateRange : query.getRanges() )
        {
            String from = dateRange.getFrom() != null ? dateRange.getFrom().toString() : null;
            String to = dateRange.getTo() != null ? dateRange.getTo().toString() : null;
            dateRangeBuilder.addRange( dateRange.getKey(), from, to );
        }

        return dateRangeBuilder;
    }

}
