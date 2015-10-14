package com.enonic.xp.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;

public abstract class NodeEvent
    implements Event
{
    private NodeId id;

    public final NodeId getId()
    {
        return this.id;
    }

    public final void setId( final NodeId id )
    {
        this.id = id;
    }
}
