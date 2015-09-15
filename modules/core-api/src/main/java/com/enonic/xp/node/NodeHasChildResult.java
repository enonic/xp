package com.enonic.xp.node;

public class NodeHasChildResult
{
    private final boolean hasChild;

    private final NodeId nodeId;

    public NodeHasChildResult( final boolean hasChild, final NodeId nodeId )
    {
        this.hasChild = hasChild;
        this.nodeId = nodeId;
    }

    public boolean hasChild()
    {
        return hasChild;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }
}
