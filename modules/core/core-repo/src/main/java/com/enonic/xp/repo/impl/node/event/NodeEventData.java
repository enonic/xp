package com.enonic.xp.repo.impl.node.event;

import java.util.Map;
import java.util.Objects;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

class NodeEventData
{
    private final NodeId nodeId;

    private final NodePath nodePath;


    private NodeEventData( final NodeId nodeId, final NodePath nodePath )
    {
        this.nodeId = nodeId;
        this.nodePath = nodePath;
    }

    static NodeEventData create( final Map<String, String> valueMap )
    {
        Objects.requireNonNull( valueMap.get( "id" ), "Expected field 'id' not found" );
        Objects.requireNonNull( valueMap.get( "path" ), "Expected field 'path' not found" );

        return new NodeEventData( NodeId.from( valueMap.get( "id" ) ), new NodePath( valueMap.get( "path" ) ) );
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
