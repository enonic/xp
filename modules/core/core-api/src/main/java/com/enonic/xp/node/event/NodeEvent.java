package com.enonic.xp.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public abstract class NodeEvent
    implements Event
{
    private NodeId id;

    private NodePath path;

    public final NodeId getId()
    {
        return this.id;
    }

    public final void setId( final NodeId id )
    {
        this.id = id;
    }

    public final NodePath getPath()
    {
        return this.path;
    }

    public final void setPath( final NodePath path )
    {
        this.path = path;
    }
}
