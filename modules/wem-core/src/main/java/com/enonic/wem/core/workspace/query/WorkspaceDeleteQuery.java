package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDeleteQuery
    extends AbstractWorkspaceQuery
{
    private EntityId entityId;

    public WorkspaceDeleteQuery( final Workspace workspace, final EntityId entityId )
    {
        super( workspace );
        this.entityId = entityId;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }
}
