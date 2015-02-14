package com.enonic.xp.query.aggregation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;

public class DateRangeAggregationQueryTest
{
    @Test
    public void testBuilder()
    {
        final Instant past = LocalDateTime.of( 1975, 8, 1, 12, 4 ).toInstant( ZoneOffset.UTC );
        final Instant future = LocalDateTime.of( 2055, 1, 1, 12, 0 ).toInstant( ZoneOffset.UTC );

        final DateRangeAggregationQuery query = DateRangeAggregationQuery.create( "dateRangeQuery" ).
            fieldName( "myFieldName" ).
            addRange( DateRange.create().
                from( past ).
                key( "myRange2" ).
                build() ).
            addRange( DateRange.create().
                key( "myRange1" ).
                to( future ).build() ).
            build();

        Assert.assertEquals( "myFieldName", query.getFieldName() );
        Assert.assertEquals( 2, query.getRanges().size() );

        final UnmodifiableIterator<DateRange> iterator = query.getRanges().iterator();
        final DateRange first = iterator.next();
        final DateRange second = iterator.next();

        Assert.assertNotSame( first, second );
    }
}
