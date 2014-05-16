package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceIdQuery
    extends AbstractWorkspaceQuery
{
    private final EntityId entityId;

    public WorkspaceIdQuery( final Workspace workspace, final EntityId entityId )
    {
        super( workspace );
        this.entityId = entityId;
    }

    public String getEntityIdAsString()
    {
        return entityId.toString();
    }

}

