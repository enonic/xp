package com.enonic.xp.node;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.OrderExpr;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NodeVersionQueryTest
{

    @Test
    void defaultOrderExpr()
    {

        final NodeVersionQuery query = NodeVersionQuery.create().
            build();

        final List<OrderExpr> orderBys = query.getOrderBys();
        assertNotNull( orderBys );

        System.out.println( orderBys );

    }
}
