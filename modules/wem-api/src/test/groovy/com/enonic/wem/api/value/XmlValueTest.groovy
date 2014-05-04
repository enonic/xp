package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class XmlValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new XmlValue( "abc" )

        expect:
        value.getObject() == "abc"
    }

    def "test asString"()
    {
        given:
        def value = new XmlValue( "abc" )

        expect:
        value.asString() == "abc"
    }
}

