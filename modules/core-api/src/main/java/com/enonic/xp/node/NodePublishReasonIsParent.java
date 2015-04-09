package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NodePublishReasonIsParent
    implements NodePublishReason
{
    private final String message = "Parent for %s";

    private final NodeId nodeId;

    public NodePublishReasonIsParent( final NodeId nodeId )
    {
        this.nodeId = nodeId;
    }

    @Override
    public NodeId getContextualNodeId()
    {
        return nodeId;
    }

    @Override
    public String getMessage()
    {
        return String.format( message, nodeId.toString() );
    }
}
