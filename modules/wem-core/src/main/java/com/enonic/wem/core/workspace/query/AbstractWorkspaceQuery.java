package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.Workspace;

public abstract class AbstractWorkspaceQuery
{
    final Workspace workspace;

    protected AbstractWorkspaceQuery( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public String getWorkspaceName()
    {
        return workspace.getName();
    }
}
