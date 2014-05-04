package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DoubleValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new DoubleValue( 11.2 )

        expect:
        value.getObject() == 11.2
    }

    def "test asString"()
    {
        given:
        def value = new DoubleValue( 11.2 )

        expect:
        value.asString() == "11.2"
    }
}

