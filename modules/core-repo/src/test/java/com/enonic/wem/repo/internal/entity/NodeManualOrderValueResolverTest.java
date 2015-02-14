package com.enonic.wem.repo.internal.entity;

import java.util.LinkedHashSet;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.xp.core.node.NodeId;

import static org.junit.Assert.*;

public class NodeManualOrderValueResolverTest
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

        final LinkedHashSet<NodeManualOrderValueResolver.NodeIdOrderValue> resolvedOrder = NodeManualOrderValueResolver.resolve( nodeIds );

        double previousValue = NodeManualOrderValueResolver.START_ORDER_VALUE + 1;

        for ( final NodeManualOrderValueResolver.NodeIdOrderValue nodeIdOrderValue : resolvedOrder )
        {
            assertTrue( previousValue > nodeIdOrderValue.getManualOrderValue() );
            previousValue = nodeIdOrderValue.getManualOrderValue();
        }
    }
}