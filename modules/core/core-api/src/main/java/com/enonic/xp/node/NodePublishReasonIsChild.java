package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodePublishReasonIsChild
    implements NodePublishReason
{
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
        return String.format( "Child of %s", nodeId );
    }
}