package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BooleanValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new BooleanValue( true )

        expect:
        value.getObject()
    }

    def "test asString"()
    {
        given:
        def value = new BooleanValue( true )

        expect:
        value.asString() == "true"
    }
}

