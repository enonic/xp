package com.enonic.wem.repo.internal.entity;

import java.util.LinkedList;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;

public class NodeManualOrderValueResolver
{
    public static final Long ORDER_SPACE = (long) Integer.MAX_VALUE;

    public static final Long START_ORDER_VALUE = 0L;

    public static LinkedList<NodeIdOrderValue> resolve( final LinkedList<NodeId> orderedNodeIds )
    {
        final LinkedList<NodeIdOrderValue> result = Lists.newLinkedList();

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
