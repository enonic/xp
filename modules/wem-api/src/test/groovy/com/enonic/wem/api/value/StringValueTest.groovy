package com.enonic.wem.api.value

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StringValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def value = new StringValue( "abc" )

        expect:
        value.getObject() == "abc"
    }

    def "test asString"()
    {
        given:
        def value = new StringValue( "abc" )

        expect:
        value.asString() == "abc"
    }

    def "test create from null"()
    {
        when:
        new StringValue( null );

        then:
        def error = thrown( ValueException.class )
        error.message == "Null value not allowed for [String]"
    }
}

