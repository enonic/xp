package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

import static org.junit.jupiter.api.Assertions.*;

public class NodeManualOrderValueResolverTest
{

    @Test
    public void resolve()
        throws Exception
    {
        List<NodeId> nodeIds = new ArrayList<>();

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
