package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class CompareEntityQuery
{

    private final EntityId entityId;

    private final Workspace source;

    private final Workspace target;


    public CompareEntityQuery( final EntityId entityId, final Workspace source, final Workspace target )
    {
        this.entityId = entityId;
        this.source = source;
        this.target = target;
    }


    public EntityId getEntityId()
    {
        return entityId;
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }
}
