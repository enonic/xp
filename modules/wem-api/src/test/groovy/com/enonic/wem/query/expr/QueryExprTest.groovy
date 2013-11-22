package com.enonic.wem.query.expr

import spock.lang.Specification

class QueryExprTest extends Specification
{
    def "test empty query"( )
    {
        given:
        def expr = new QueryExpr( null, null )

        expect:
        expr.getConstraint() == null
        expr.getOrderList() == []
        expr.toString() == ""
    }

    def "test full query"( )
    {
        given:
        def constraint = CompareExpr.eq( new FieldExpr( "a" ), ValueExpr.number( 2 ) )
        def orderList = [new FieldOrderExpr( new FieldExpr( "a" ), OrderExpr.Direction.DESC )]
        def expr = new QueryExpr( constraint, orderList )

        expect:
        expr.getConstraint() == constraint
        expr.getOrderList() == orderList
        expr.toString() == "a = 2.0 ORDER BY a DESC"
    }

    def "test only constraint in query"( )
    {
        given:
        def constraint = CompareExpr.eq( new FieldExpr( "a" ), ValueExpr.number( 2 ) )
        def expr = new QueryExpr( constraint, null )

        expect:
        expr.getConstraint() == constraint
        expr.getOrderList() == []
        expr.toString() == "a = 2.0"
    }

    def "test only order in query"( )
    {
        given:
        def orderList = [new FieldOrderExpr( new FieldExpr( "a" ), OrderExpr.Direction.DESC )]
        def expr = new QueryExpr( null, orderList )

        expect:
        expr.getConstraint() == null
        expr.getOrderList() == orderList
        expr.toString() == "ORDER BY a DESC"
    }
}
