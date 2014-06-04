package com.enonic.wem.core.workspace;

import javax.inject.Inject;

import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDiffResolverImpl
    implements WorkspaceDiffResolver
{

    @Inject
    private WorkspaceStore workspaceStore;


    @Override
    public WorkspaceDiff resolve( final Workspace source, final Workspace target )
    {

        return null;

    }
}
