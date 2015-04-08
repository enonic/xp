package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
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
