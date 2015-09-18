package com.enonic.wem.repo.internal.index.query;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class NodeQueryResultSet
{
    private final LinkedHashSet<NodeId> nodeIds;

    private final NodeId first;

    private final NodeId last;

    private NodeQueryResultSet( LinkedHashSet<NodeId> nodeIds, final NodeId first, final NodeId last )
    {
        this.nodeIds = nodeIds;
        this.first = first;
        this.last = last;
    }

    public static NodeQueryResultSet empty()
    {
        return new NodeQueryResultSet( Sets.newLinkedHashSet(), null, null );
    }

    public LinkedHashSet<NodeId> getNodeIds()
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

    public static NodeQueryResultSet from( final Set<NodeQueryResultEntry> entries )
    {
        if ( entries.isEmpty() )
        {
            return new NodeQueryResultSet( Sets.newLinkedHashSet(), null, null );
        }

        final NodeId first = entries.iterator().next().getId();

        final LinkedHashSet<NodeId> nodeIds = Sets.newLinkedHashSet();

        NodeId last = null;

        for ( final NodeQueryResultEntry entry : entries )
        {
            last = entry.getId();
            nodeIds.add( last );
        }

        return new NodeQueryResultSet( nodeIds, first, last );
    }
}
