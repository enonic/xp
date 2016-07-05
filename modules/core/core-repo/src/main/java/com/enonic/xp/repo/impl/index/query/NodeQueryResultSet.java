package com.enonic.xp.repo.impl.index.query;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class NodeQueryResultSet
{
    private final List<NodeId> nodeIds;

    private final NodeId first;

    private final NodeId last;

    private NodeQueryResultSet( List<NodeId> nodeIds, final NodeId first, final NodeId last )
    {
        this.nodeIds = nodeIds;
        this.first = first;
        this.last = last;
    }

    public static NodeQueryResultSet empty()
    {
        return new NodeQueryResultSet( Lists.newArrayList(), null, null );
    }

    public static NodeQueryResultSet from( final List<NodeQueryResultEntry> entries )
    {
        if ( entries.isEmpty() )
        {
            return new NodeQueryResultSet( Lists.newArrayList(), null, null );
        }

        final NodeId first = entries.iterator().next().getId();

        final List<NodeId> nodeIds = Lists.newArrayList();

        NodeId last = null;

        for ( final NodeQueryResultEntry entry : entries )
        {
            last = entry.getId();
            nodeIds.add( last );
        }

        return new NodeQueryResultSet( nodeIds, first, last );
    }

    public List<NodeId> getNodeIds()
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
}
