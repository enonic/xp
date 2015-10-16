package com.enonic.xp.event;

import java.io.Externalizable;

public abstract class DistributedEvent
    implements Event, Externalizable
{
    private boolean isRemote = false;

    public final boolean isRemote()
    {
        return isRemote;
    }

    public final void setIsRemote( final boolean isRemote )
    {
        this.isRemote = isRemote;
    }
}
