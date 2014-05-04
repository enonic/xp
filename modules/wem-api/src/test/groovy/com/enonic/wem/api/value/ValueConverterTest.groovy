package com.enonic.wem.api.value

import com.enonic.wem.api.util.GeoPoint
import org.mockito.Mockito
import spock.lang.Specification

import java.awt.*

class ValueConverterTest
    extends Specification
{
    def "test convert any to string"()
    {
        given:
        def object = Mockito.mock( Object.class )
        Mockito.when( object.toString() ).thenReturn( "anyObject" )

        def result = ValueConverter.convert( object, String.class )

        expect:
        result == "anyObject"
    }

    def "test no converter"()
    {
        when:
        ValueConverter.convert( "test", Font.class )

        then:
        def error = thrown( ValueException.class )
        error.message == "Conversion of java.lang.String to java.awt.Font is not supported"
    }

    def "test geoPoint convert failure"()
    {
        when:
        ValueConverter.convert( "91,181", GeoPoint.class )

        then:
        def error = thrown( ValueException.class )
        error.message == "Could not convert [java.lang.String] to [com.enonic.wem.api.util.GeoPoint] " +
            "(java.lang.IllegalArgumentException: Latitude [91.0] is not within range [-90.0‥90.0])"
    }
}
