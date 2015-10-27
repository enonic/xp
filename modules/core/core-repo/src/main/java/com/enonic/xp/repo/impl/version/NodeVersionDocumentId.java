package com.enonic.xp.repo.impl.version;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class NodeVersionDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    public NodeVersionDocumentId( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( nodeVersionId );

        this.nodeId = nodeId;
        this.nodeVersionId = nodeVersionId;

        this.value = nodeId + SEPARATOR + nodeVersionId.toString();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    @Override
    public String toString()
    {
        return value;
    }

}
