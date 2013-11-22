package com.enonic.wem.query.expr

import spock.lang.Specification

class DynamicOrderExprTest extends Specification
{
    def "test dynamic order expression"( )
    {
        given:
        def func = new FunctionExpr( "name", [] )
        def expr = new DynamicOrderExpr( func, OrderExpr.Direction.DESC )

        expect:
        expr.getFunction() == func
        expr.getDirection() == OrderExpr.Direction.DESC
        expr.toString() == "name() DESC"
    }
}
