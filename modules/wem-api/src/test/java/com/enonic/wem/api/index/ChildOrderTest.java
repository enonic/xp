package com.enonic.wem.api.index;

import org.junit.Test;

import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;

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
            add( FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY, OrderExpr.Direction.ASC ) ).
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
            add( FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY, OrderExpr.Direction.DESC ) ).
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
            add( FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY.toLowerCase(), OrderExpr.Direction.ASC ) ).
            build().
            isManualOrder() );

        assertTrue( ChildOrder.create().
            add( FieldOrderExpr.create( IndexPaths.MANUAL_ORDER_VALUE_KEY.toUpperCase(), OrderExpr.Direction.ASC ) ).
            build().
            isManualOrder() );

    }


}