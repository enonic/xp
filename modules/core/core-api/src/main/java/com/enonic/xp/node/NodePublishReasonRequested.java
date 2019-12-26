package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
