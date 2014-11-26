package com.enonic.wem.api.query.expr

import spock.lang.Specification

class LogicalExprTest
        extends Specification
{
    def "test AND expression"()
    {
        given:
        def left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) )
        def right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) )
        def expr = LogicalExpr.and( left, right )

        expect:
        expr.getLeft() == left
        expr.getRight() == right
        expr.getOperator() == LogicalExpr.Operator.AND
        expr.toString() == "(a = 2.0 AND b = 2.0)"
    }

    def "test OR expression"()
    {
        given:
        def left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) )
        def right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) )
        def expr = LogicalExpr.or( left, right )

        expect:
        expr.getLeft() == left
        expr.getRight() == right
        expr.getOperator() == LogicalExpr.Operator.OR
        expr.toString() == "(a = 2.0 OR b = 2.0)"
    }
}
