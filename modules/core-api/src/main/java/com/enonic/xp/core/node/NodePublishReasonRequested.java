package com.enonic.xp.core.node;

public class NodePublishReasonRequested
    implements NodePublishReason
{
    public NodePublishReasonRequested()
    {
    }

    @Override
    public NodeId getContextualNodeId()
    {
        return null;
    }

    @Override
    public String getMessage()
    {
        return "";
    }
}
