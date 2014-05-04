package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class LongValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new LongValue( 11 )

        expect:
        value.getObject() == 11
    }

    def "test asString"()
    {
        given:
        def value = new LongValue( 11 )

        expect:
        value.asString() == "11"
    }
}

