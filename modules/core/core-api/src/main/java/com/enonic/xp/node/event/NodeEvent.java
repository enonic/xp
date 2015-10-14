package com.enonic.xp.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;

public abstract class NodeEvent
    implements Event
{
    private NodeId id;

    public NodeId getId()
    {
        return this.id;
    }

    public void setId( final NodeId id )
    {
        this.id = id;
    }
}
