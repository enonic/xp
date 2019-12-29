package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface NodePublishReason
{
    NodeId getContextualNodeId();

    String getMessage();
}
