package com.enonic.xp.node;

public interface PushNodesListener
{
    enum PushResult
    {
        PUSHED, FAILED
    }

    void nodePushed( NodeId nodeId, PushResult result );
}
