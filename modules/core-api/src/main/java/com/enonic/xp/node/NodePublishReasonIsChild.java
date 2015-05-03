package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NodePublishReasonIsChild
    implements NodePublishReason
{
    private final String message = "Child of %s";

    private final NodeId nodeId;

    public NodePublishReasonIsChild( final NodeId nodeId )
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
