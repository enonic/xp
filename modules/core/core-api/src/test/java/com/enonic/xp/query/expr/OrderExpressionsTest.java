package com.enonic.xp.query.expr;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.index.IndexPath;

import static org.junit.Assert.*;

public class OrderExpressionsTest
{
    @Test
    public void testBuilder()
    {
        final FieldOrderExpr expr1 = FieldOrderExpr.create( IndexPath.from( "name" ), OrderExpr.Direction.DESC );
        final FieldOrderExpr expr2 = FieldOrderExpr.create( IndexPath.from( "date" ), OrderExpr.Direction.ASC );

        final OrderExpressions orderExprs = OrderExpressions.create().
            add( expr1 ).
            add( expr2 ).
            build();

        assertNotNull( orderExprs );
        assertEquals( 2, orderExprs.getSize() );
        assertTrue( orderExprs.contains( expr1 ) );
        assertTrue( orderExprs.contains( expr2 ) );
    }

    @Test
    public void empty()
    {
        final OrderExpressions orderExprs = OrderExpressions.empty();

        assertTrue( orderExprs.isEmpty() );
    }

    @Test
    public void from()
    {
        final FieldOrderExpr expr1 = FieldOrderExpr.create( "name", OrderExpr.Direction.DESC );
        final FieldOrderExpr expr2 = FieldOrderExpr.create( "date", OrderExpr.Direction.ASC );
        final FieldOrderExpr expr3 = FieldOrderExpr.create( "time", OrderExpr.Direction.ASC );

        final OrderExpressions orderExprs1 = OrderExpressions.from( expr1, expr2 );
        final OrderExpressions orderExprs2 = OrderExpressions.from( Lists.newArrayList( expr3 ) );

        assertNotNull( orderExprs1 );
        assertNotNull( orderExprs2 );
        assertEquals( 2, orderExprs1.getSize() );
        assertEquals( 1, orderExprs2.getSize() );
    }
}
