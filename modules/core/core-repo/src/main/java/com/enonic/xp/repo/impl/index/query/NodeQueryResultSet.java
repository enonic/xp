package com.enonic.xp.repo.impl.index.query;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class NodeQueryResultSet
{
    private final LinkedList<NodeId> nodeIds;

    private final NodeId first;

    private final NodeId last;

    private NodeQueryResultSet( LinkedList<NodeId> nodeIds, final NodeId first, final NodeId last )
    {
        this.nodeIds = nodeIds;
        this.first = first;
        this.last = last;
    }

    public static NodeQueryResultSet empty()
    {
        return new NodeQueryResultSet( Lists.newLinkedList(), null, null );
    }

    public LinkedList<NodeId> getNodeIds()
    {
        return nodeIds;
    }

    public NodeId first()
    {
        return first;
    }

    public NodeId last()
    {
        return last;
    }

    public boolean isEmpty()
    {
        return nodeIds.isEmpty();
    }

    public NodeIds asNodeIds()
    {
        return NodeIds.from( nodeIds );
    }

    public int size()
    {
        return this.nodeIds.size();
    }

    public static NodeQueryResultSet from( final List<NodeQueryResultEntry> entries )
    {
        if ( entries.isEmpty() )
        {
            return new NodeQueryResultSet( Lists.newLinkedList(), null, null );
        }

        final NodeId first = entries.iterator().next().getId();

        final LinkedList<NodeId> nodeIds = Lists.newLinkedList();

        NodeId last = null;

        for ( final NodeQueryResultEntry entry : entries )
        {
            last = entry.getId();
            nodeIds.add( last );
        }

        return new NodeQueryResultSet( nodeIds, first, last );
    }
}
