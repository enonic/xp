package com.enonic.wem.api.query.aggregation

import org.joda.time.DateTime
import spock.lang.Specification

class DateRangeTest
    extends Specification
{

    def "test builder"()
    {
        given:
        DateTime now = DateTime.now()

        when:
        final DateRange fromRange = Range.newDateRange().key( "myKey" ).from( now ).build()

        then:
        fromRange.getKey() == "myKey"
        fromRange.getFrom() == now;
        fromRange.getTo() == null;
    }

}
