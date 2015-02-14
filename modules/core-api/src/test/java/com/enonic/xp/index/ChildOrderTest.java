package com.enonic.xp.index;

import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

import static org.junit.Assert.*;

public class ChildOrderTest
{

    @Test
    public void manual_order_asc()
        throws Exception
    {
        assertTrue( ChildOrder.manualOrder().isManualOrder() );
        assertFalse( ChildOrder.defaultOrder().isManualOrder() );
        assertFalse( ChildOrder.create().build().isManualOrder() );
        assertTrue( ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).
            build().
            isManualOrder() );
    }

    @Test
    public void manual_order_desc()
        throws Exception
    {
        assertTrue( ChildOrder.manualOrder().isManualOrder() );
        assertFalse( ChildOrder.defaultOrder().isManualOrder() );
        assertFalse( ChildOrder.create().build().isManualOrder() );
        assertTrue( ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC ) ).
            build().
            isManualOrder() );
    }

    @Test
    public void manual_order_ignorecase()
        throws Exception
    {
        assertTrue( ChildOrder.manualOrder().isManualOrder() );
        assertFalse( ChildOrder.defaultOrder().isManualOrder() );
        assertFalse( ChildOrder.create().build().isManualOrder() );
        assertTrue( ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).
            build().
            isManualOrder() );

        assertTrue( ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).
            build().
            isManualOrder() );

    }


}