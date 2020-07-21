package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodePublishReasonIsParent
    implements NodePublishReason
{
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
        return String.format( "Parent for %s", nodeId );
    }
}
