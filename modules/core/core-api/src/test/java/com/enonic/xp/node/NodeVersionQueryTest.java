package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.query.expr.OrderExpr;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NodeVersionQueryTest
{

    @Test
    public void defaultOrderExpr()
        throws Exception
    {

        final NodeVersionQuery query = NodeVersionQuery.create().
            build();

        final ImmutableList<OrderExpr> orderBys = query.getOrderBys();
        assertNotNull( orderBys );

        System.out.println( orderBys );

    }
}
