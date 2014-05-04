package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class HtmlPartValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new HtmlPartValue( "abc" )

        expect:
        value.getObject() == "abc"
    }

    def "test asString"()
    {
        given:
        def value = new HtmlPartValue( "abc" )

        expect:
        value.asString() == "abc"
    }
}

