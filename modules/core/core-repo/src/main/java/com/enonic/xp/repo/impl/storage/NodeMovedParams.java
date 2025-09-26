package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class NodeMovedParams
{
    private final NodePath previousPath;

    private final NodePath newPath;

    private final NodeId nodeId;

    public NodeMovedParams( final NodePath previousPath, final NodePath newPath, final NodeId nodeId )
    {
        this.previousPath = previousPath;
        this.newPath = newPath;
        this.nodeId = nodeId;
    }

    public NodePath getPreviousPath()
    {
        return previousPath;
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


