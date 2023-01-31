package com.enonic.xp.core.impl.content.event;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.PushNodesResult;

public interface ContentEventProducer
{
    void published( final PushNodesResult result );

    void unpublished( final NodeBranchEntries entries );
}
