package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeIdExistsException
    extends RuntimeException
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    public NodeIdExistsException( final NodeId nodeId, final NodePath nodePath )
    {
        super( "Node " + nodeId + " at path " + nodePath + " already exists" );
        this.nodeId = nodeId;
        this.nodePath = nodePath;
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
