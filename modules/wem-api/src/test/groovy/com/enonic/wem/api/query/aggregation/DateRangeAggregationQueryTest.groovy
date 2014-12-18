package com.enonic.wem.api.query.aggregation

import com.google.common.collect.UnmodifiableIterator
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateRangeAggregationQueryTest
    extends Specification
{
    def "test builder"()
    {
        def Instant past = LocalDateTime.of( 1975, 8, 1, 12, 04 ).toInstant( ZoneOffset.UTC );
        def Instant future = LocalDateTime.of( 2055, 01, 01, 12, 00 ).toInstant( ZoneOffset.UTC );

        when:
        DateRangeAggregationQuery query = DateRangeAggregationQuery.create( "dateRangeQuery" ).
            fieldName( "myFieldName" ).
            addRange( DateRange.create().
                       from( past ).
                       key( "myRange2" ).
                       build() ).
            addRange( DateRange.create().
                       key( "myRange1" ).
                       to( future ).build() ).
            build();

        then:
        query.getFieldName() == "myFieldName"
        query.getRanges().size() == 2
        UnmodifiableIterator<DateRange> iterator = query.getRanges().iterator()
        DateRange first = iterator.next()
        DateRange second = iterator.next();
        first != second
        first.key == "myRange1" ? second.key == "myRange2" : first.key == "myRange2" && second.key == "myRange1"
        first.from == past ? second.to == future : second.from == past && first.to == future
    }
}
