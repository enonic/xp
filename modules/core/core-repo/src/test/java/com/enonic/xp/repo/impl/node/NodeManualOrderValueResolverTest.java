package com.enonic.xp.repo.impl.node;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

import static org.junit.Assert.*;

public class NodeManualOrderValueResolverTest
{

    @Test
    public void resolve()
        throws Exception
    {
        LinkedList<NodeId> nodeIds = Lists.newLinkedList();

        for ( int i = 0; i <= 10; i++ )
        {
            nodeIds.add( NodeId.from( i ) );
        }

        final List<NodeManualOrderValueResolver.NodeIdOrderValue> resolvedOrder =
            NodeManualOrderValueResolver.resolve( NodeIds.from( nodeIds ) );

        double previousValue = NodeManualOrderValueResolver.START_ORDER_VALUE + 1;

        for ( final NodeManualOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : resolvedOrder )
        {
            assertTrue( previousValue > nodeIdOrderValue.getManualOrderValue() );
            previousValue = nodeIdOrderValue.getManualOrderValue();
        }
    }
}