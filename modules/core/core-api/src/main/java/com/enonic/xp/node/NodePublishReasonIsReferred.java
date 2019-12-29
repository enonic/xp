package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodePublishReasonIsReferred
    implements NodePublishReason
{
    private final String message = "Referred from %s";

    private final NodeId nodeId;

    public NodePublishReasonIsReferred( final NodeId nodeId )
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
