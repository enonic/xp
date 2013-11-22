package com.enonic.wem.query.expr

import spock.lang.Specification

class NotExprTest extends Specification
{
    def "test NOT expression"( )
    {
        given:
        def inner = CompareExpr.eq( new FieldExpr( "a" ), ValueExpr.number( 2 ) )
        def expr = new NotExpr( inner )

        expect:
        expr.getExpression() == inner
        expr.toString() == "NOT (a = 2.0)"
    }
}
