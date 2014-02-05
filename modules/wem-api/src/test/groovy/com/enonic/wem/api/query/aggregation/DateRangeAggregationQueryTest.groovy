package com.enonic.wem.api.query.aggregation

import com.google.common.collect.UnmodifiableIterator
import org.joda.time.DateTime
import spock.lang.Specification

class DateRangeAggregationQueryTest
    extends Specification
{
    def "test builder"()
    {
        def DateTime past = new DateTime( 1975, 8, 1, 12, 04 )
        def DateTime future = new DateTime( 2055, 01, 01, 12, 00 )

        when:
        DateRangeAggregationQuery query = RangeAggregationQuery.newDateRangeAggregationQuery().
            fieldName( "myFieldName" ).
            range( Range.newDateRange().
                       from( past ).
                       key( "myRange2" ).
                       build() ).
            range( Range.newDateRange().
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
