package com.enonic.xp.query.expr;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class QueryExprTest
{
    @Test
    public void empty_query()
    {
        final QueryExpr expr = new QueryExpr( null, null );

        assertNull( expr.getConstraint() );
        assertEquals( 0, expr.getOrderList().size() );
        assertEquals( "", expr.toString() );
    }

    @Test
    public void full_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final List<OrderExpr> orderList = List.of( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr = new QueryExpr( constraint, orderList );

        assertSame( constraint, expr.getConstraint() );
        assertEquals( 1, expr.getOrderList().size() );
        assertEquals( "a = 2.0 ORDER BY a DESC", expr.toString() );
    }

    @Test
    public void only_constraint_in_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final QueryExpr expr = new QueryExpr( constraint, null );

        assertSame( constraint, expr.getConstraint() );
        assertEquals( 0, expr.getOrderList().size() );
        assertEquals( "a = 2.0", expr.toString() );
    }

    @Test
    public void only_order_in_query()
    {
        final List<OrderExpr> orderList = List.of( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr1 = new QueryExpr( null, orderList );
        final QueryExpr expr2 = new QueryExpr( orderList );
        final QueryExpr expr3 = QueryExpr.from( null, orderList );

        assertNull( expr1.getConstraint() );
        assertEquals( 1, expr1.getOrderList().size() );
        assertEquals( "ORDER BY a DESC", expr1.toString() );

        assertNull( expr2.getConstraint() );
        assertEquals( 1, expr2.getOrderList().size() );
        assertEquals( "ORDER BY a DESC", expr2.toString() );

        assertNull( expr3.getConstraint() );
        assertEquals( 1, expr3.getOrderList().size() );
        assertEquals( "ORDER BY a DESC", expr3.toString() );
    }
}
