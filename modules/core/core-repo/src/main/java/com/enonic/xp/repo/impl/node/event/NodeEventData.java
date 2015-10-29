package com.enonic.xp.repo.impl.node.event;

import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

class NodeEventData
{
    private final NodeId nodeId;

    private final NodePath nodePath;


    public NodeEventData( final NodeId nodeId, final NodePath nodePath )
    {
        this.nodeId = nodeId;
        this.nodePath = nodePath;
    }

    static NodeEventData create( final Map<String, String> valueMap )
    {
        Preconditions.checkNotNull( valueMap.get( "id" ), "Expected field 'id' not found" );
        Preconditions.checkNotNull( valueMap.get( "path" ), "Expected field 'path' not found" );

        return new NodeEventData( NodeId.from( valueMap.get( "id" ) ), NodePath.create( valueMap.get( "path" ) ).build() );
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }
}
