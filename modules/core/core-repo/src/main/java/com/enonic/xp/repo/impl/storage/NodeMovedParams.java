package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class NodeMovedParams
{
    private final NodePath existingPath;

    private final NodePath newPath;

    private final NodeId nodeId;

    public NodeMovedParams( final NodePath existingPath, final NodePath newPath, final NodeId nodeId )
    {
        this.existingPath = existingPath;
        this.newPath = newPath;
        this.nodeId = nodeId;
    }

    public NodePath getExistingPath()
    {
        return existingPath;
    }

    public NodePath getNewPath()
    {
        return newPath;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }
}


