package com.enonic.wem.core.elasticsearch;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDocumentId
{
    private final String value;

    public WorkspaceDocumentId( final EntityId entityId, final Workspace workspace )
    {
        Preconditions.checkNotNull( entityId );
        Preconditions.checkNotNull( workspace );

        this.value = entityId + "-" + workspace.getName();
    }
}
