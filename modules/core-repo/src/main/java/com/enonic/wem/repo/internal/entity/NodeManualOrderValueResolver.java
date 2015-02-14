package com.enonic.wem.repo.internal.entity;

import java.util.LinkedHashSet;

import com.google.common.collect.Sets;

import com.enonic.xp.core.node.NodeId;

public class NodeManualOrderValueResolver
{
    public static final Long ORDER_SPACE = (long) Integer.MAX_VALUE;

    public static final Long START_ORDER_VALUE = 0l;

    public static LinkedHashSet<NodeIdOrderValue> resolve( final LinkedHashSet<NodeId> orderedNodeIds )
    {
        final LinkedHashSet<NodeIdOrderValue> result = Sets.newLinkedHashSet();

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
        private Long manualOrderValue;

        private NodeId nodeId;

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
