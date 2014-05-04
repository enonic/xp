package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneOffset

@Unroll
class DateTimeValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def real = LocalDateTime.of( 2013, 1, 2, 11, 22, 33 ).toInstant( ZoneOffset.UTC );
        def value = new DateTimeValue( real )

        expect:
        value.getObject() == real
    }

    def "test asString"()
    {
        given:
        def real = LocalDateTime.of( 2013, 1, 2, 11, 22, 33 ).toInstant( ZoneOffset.UTC );
        def value = new DateTimeValue( real )

        expect:
        value.asString() == "2013-01-02T11:22:33Z"
    }
}
