package com.enonic.wem.api.query.expr

import com.enonic.wem.api.data.ValueTypes
import spock.lang.Specification
import spock.lang.Unroll

class ValueExprTest
    extends Specification
{
    @Unroll
    def "test quote for string (#value)"()
    {
        given:
        def expr = ValueExpr.string( value )

        expect:
        expr != null
        expr.toString() == expected

        where:
        value   | expected
        "test"  | "'test'"
        "te'st" | '"te\'st"'
        'te"st' | "'te\"st'"
    }

    def "test string value"()
    {
        given:
        def expr = ValueExpr.string( "value" )

        expect:
        expr != null
        expr.toString() == "'value'"
        expr.getValue().getType() == ValueTypes.STRING
    }

    def "test number value"()
    {
        given:
        def expr = ValueExpr.number( 33 )

        expect:
        expr != null
        expr.toString() == "33.0"
        expr.getValue().getType() == ValueTypes.DOUBLE
    }

    def "test dateTime value"()
    {
        given:
        def expr = ValueExpr.instant( '2013-11-11T22:22:22.000Z' )

        expect:
        expr != null
        expr.toString() == "instant('2013-11-11T22:22:22Z')"
        expr.getValue().getType() == ValueTypes.DATE_TIME
    }

    def "test geoPoint value"()
    {
        given:
        def expr = ValueExpr.geoPoint( '11,22' )

        expect:
        expr != null
        expr.toString() == "geoPoint('11.0,22.0')"
        expr.getValue().getType() == ValueTypes.GEO_POINT
    }
}
