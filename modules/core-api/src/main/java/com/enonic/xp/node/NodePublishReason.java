package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public interface NodePublishReason
{
    NodeId getContextualNodeId();

    String getMessage();
}
