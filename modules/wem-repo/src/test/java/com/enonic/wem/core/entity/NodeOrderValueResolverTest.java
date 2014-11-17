package com.enonic.wem.core.entity;

import java.util.LinkedHashSet;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.api.node.NodeId;

import static org.junit.Assert.*;

public class NodeOrderValueResolverTest
{

    @Test
    public void resolve()
        throws Exception
    {
        LinkedHashSet<NodeId> nodeIds = Sets.newLinkedHashSet();

        for ( int i = 0; i <= 10; i++ )
        {
            nodeIds.add( NodeId.from( i ) );
        }

        final LinkedHashSet<NodeOrderValueResolver.NodeIdOrderValue> resolvedOrder = NodeOrderValueResolver.resolve( nodeIds );

        double previousValue = NodeOrderValueResolver.START_ORDER_VALUE + 1;

        for ( final NodeOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : resolvedOrder )
        {
            assertTrue( previousValue > nodeIdOrderValue.getManualOrderValue() );
            previousValue = nodeIdOrderValue.getManualOrderValue();
        }
    }
}