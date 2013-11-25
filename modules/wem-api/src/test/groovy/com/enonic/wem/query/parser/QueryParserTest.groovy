package com.enonic.wem.query.parser

import com.enonic.wem.query.QueryException
import spock.lang.Specification
import spock.lang.Unroll

class QueryParserTest extends Specification
{
    @Unroll
    def "test comparison (#query)"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query    | expected
        "a = 3"  | "a = 3.0"
        "a != 3" | "a != 3.0"
        "a > 3"  | "a > 3.0"
        "a >= 3" | "a >= 3.0"
        "a < 3"  | "a < 3.0"
        "a <= 3" | "a <= 3.0"
    }

    def "test invalid query (#query)"( )
    {
        when:
        QueryParser.parse( "a - 3" )

        then:
        thrown( QueryException )
    }


    @Unroll
    def "test LIKE compare (#query)"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query            | expected
        "a like '3'"     | "a LIKE '3'"
        "a not LIKE '3'" | "a NOT LIKE '3'"
    }

    @Unroll
    def "test IN compare (#query)"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query              | expected
        "a IN ('3')"       | "a IN ('3')"
        "a NOT in ('3')"   | "a NOT IN ('3')"
        "a IN (1, 2, '3')" | "a IN (1.0, 2.0, '3')"
    }

    def "test geoPoint function"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a = geoPoint('1,1')"
        expected = "a = geoPoint('1,1')"
    }

    def "test dateTime function"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a = dateTime('2013-11-11T22:22:22')"
        expected = "a = dateTime('2013-11-11T22:22:22')"
    }

    def "test NOT expression"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "NOT(a > 3 AND b = 3)"
        expected = "NOT ((a > 3.0 AND b = 3.0))"
    }

    def "test AND expression"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a > 3 AND b = 3 AND c = 3"
        expected = "((a > 3.0 AND b = 3.0) AND c = 3.0)"
    }

    def "test OR expression"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a > 3 OR b = 3 OR c = 3"
        expected = "((a > 3.0 OR b = 3.0) OR c = 3.0)"
    }

    def "test AND and OR expression"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a > 3 AND b = 3 OR c = 3"
        expected = "((a > 3.0 AND b = 3.0) OR c = 3.0)"
    }

    def "test dynamic constraint"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a > 3 AND geoLocation('arg1', 2)"
        expected = "(a > 3.0 AND geoLocation('arg1', 2.0))"
    }

    @Unroll
    def "test field order (#query)"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query                    | expected
        "ORDER BY a"             | "ORDER BY a ASC"
        "order BY a DESC"        | "ORDER BY a DESC"
        "ORDER BY a, b"          | "ORDER BY a ASC, b ASC"
        "order by a DESC, b ASC" | "ORDER BY a DESC, b ASC"
    }

    @Unroll
    def "test dynamic order (#query)"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query                              | expected
        "ORDER BY score()"                 | "ORDER BY score() ASC"
        "order BY geoLocation('arg') DESC" | "ORDER BY geoLocation('arg') DESC"
        "ORDER BY a, score()"              | "ORDER BY a ASC, score() ASC"
        "order by score() DESC, a"         | "ORDER BY score() DESC, a ASC"
    }

    def "test empty query"( )
    {
        given:
        def expr = QueryParser.parse( "" )

        expect:
        expr != null
        expr.toString() == ""
    }

    def "test full query"( )
    {
        given:
        def expr = QueryParser.parse( query )

        expect:
        expr != null
        expr.toString() == expected

        where:
        query = "a > 3 ORDER BY a DESC"
        expected = "a > 3.0 ORDER BY a DESC"
    }

    def "test illegal value function"( )
    {
        when:
        QueryParser.parse( "a = badFunc()" )

        then:
        thrown( QueryException )
    }
}
