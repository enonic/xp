package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Unroll
class DateValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def real = LocalDate.of( 2013, 1, 2 );
        def value = new DateValue( real )

        expect:
        value.getObject() == real
    }

    def "test asString"()
    {
        given:
        def real = LocalDate.of( 2013, 1, 2 );
        def value = new DateValue( real )

        expect:
        value.asString() == "2013-01-02"
    }
}
