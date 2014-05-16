package com.enonic.wem.core.workspace;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDeleteDocument
{

    private final Workspace workspace;

    private final EntityId entityId;

    public WorkspaceDeleteDocument( final Workspace workspace, final EntityId entityId )
    {
        this.workspace = workspace;
        this.entityId = entityId;
    }
}
