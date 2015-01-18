package com.enonic.wem.api.query.aggregation

import spock.lang.Specification

import java.time.Instant

class DateRangeTest
    extends Specification
{

    def "test builder"()
    {
        given:
        Instant now = Instant.now()

        when:
        final DateRange fromRange = DateRange.create().key( "myKey" ).from( now ).build()

        then:
        fromRange.getKey() == "myKey"
        fromRange.getFrom() == now;
        fromRange.getTo() == null;
    }

}
