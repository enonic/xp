package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class PushNodeArguments
{
    private final Workspace to;

    private final EntityId id;

    public PushNodeArguments( final Workspace to, final EntityId id )
    {
        this.to = to;
        this.id = id;
    }

    public Workspace getTo()
    {
        return to;
    }

    public EntityId getId()
    {
        return id;
    }
}
