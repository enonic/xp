package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class NodeManualOrderValueResolver
{
    public static final Long ORDER_SPACE = (long) Integer.MAX_VALUE;

    public static final Long START_ORDER_VALUE = 0L;

    public static List<NodeIdOrderValue> resolve( final NodeIds orderedNodeIds )
    {
        final List<NodeIdOrderValue> result = new ArrayList<>();

        Long currentValue = START_ORDER_VALUE;

        for ( final NodeId nodeId : orderedNodeIds )
        {
            result.add( new NodeIdOrderValue( nodeId, currentValue ) );
            currentValue = currentValue - ORDER_SPACE;
        }

        return result;
    }


    public static class NodeIdOrderValue
    {
        private final Long manualOrderValue;

        private final NodeId nodeId;

        public NodeIdOrderValue( final NodeId nodeId, final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            this.nodeId = nodeId;
        }

        public Long getManualOrderValue()
        {
            return manualOrderValue;
        }

        public NodeId getNodeId()
        {
            return nodeId;
        }
    }

}
