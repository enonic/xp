package com.enonic.wem.core.entity;

import java.util.LinkedHashSet;

import com.google.common.collect.Sets;

public class NodeOrderValueResolver
{
    public static final long START_ORDER_VALUE = 0l;

    public static final long ORDER_SPACE = (long) Integer.MAX_VALUE;

    public static LinkedHashSet<NodeIdOrderValue> resolve( final LinkedHashSet<NodeId> orderedNodeIds )
    {
        final LinkedHashSet<NodeIdOrderValue> result = Sets.newLinkedHashSet();

        long currentValue = START_ORDER_VALUE;

        for ( final NodeId nodeId : orderedNodeIds )
        {
            result.add( new NodeIdOrderValue( nodeId, currentValue ) );
            currentValue = currentValue - ORDER_SPACE;
        }

        return result;
    }


    public static class NodeIdOrderValue
    {
        private long manualOrderValue;

        private NodeId nodeId;

        public NodeIdOrderValue( final NodeId nodeId, final long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            this.nodeId = nodeId;
        }

        public long getManualOrderValue()
        {
            return manualOrderValue;
        }

        public NodeId getNodeId()
        {
            return nodeId;
        }
    }

}
