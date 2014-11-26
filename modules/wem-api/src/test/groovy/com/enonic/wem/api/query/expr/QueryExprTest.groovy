package com.enonic.wem.api.query.expr

import com.google.common.collect.Sets
import spock.lang.Specification

class QueryExprTest
    extends Specification
{
    def "test empty query"()
    {
        given:
        def expr = new QueryExpr( null, null )

        expect:
        expr.getConstraint() == null
        expr.getOrderSet().isEmpty()
        expr.toString() == ""
    }

    def "test full query"()
    {
        given:
        def constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) )
        def orderList = Sets.newHashSet( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) )
        def expr = new QueryExpr( constraint, orderList )

        expect:
        expr.getConstraint() == constraint
        expr.getOrderSet() == orderList
        expr.toString() == "a = 2.0 ORDER BY a DESC"
    }

    def "test only constraint in query"()
    {
        given:
        def constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) )
        def expr = new QueryExpr( constraint, null )

        expect:
        expr.getConstraint() == constraint
        expr.getOrderSet().isEmpty()
        expr.toString() == "a = 2.0"
    }

    def "test only order in query"()
    {
        given:
        def orderList = Sets.newHashSet( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) )
        def expr = new QueryExpr( null, orderList )

        expect:
        expr.getConstraint() == null
        expr.getOrderSet() == orderList
        expr.toString() == "ORDER BY a DESC"
    }
}
