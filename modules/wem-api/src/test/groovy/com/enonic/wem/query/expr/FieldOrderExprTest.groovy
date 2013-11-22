package com.enonic.wem.query.expr

import spock.lang.Specification

class FieldOrderExprTest extends Specification
{
    def "test field order expression"( )
    {
        given:
        def field = new FieldExpr( "name" )
        def expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC )

        expect:
        expr.getField() == field
        expr.getDirection() == OrderExpr.Direction.DESC
        expr.toString() == "name DESC"
    }
}
