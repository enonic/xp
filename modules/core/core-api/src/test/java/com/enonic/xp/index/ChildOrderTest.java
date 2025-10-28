package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChildOrderTest
{

    @Test
    void manual_order_asc()
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
    void manual_order_desc()
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
    void manual_order_ignorecase()
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
