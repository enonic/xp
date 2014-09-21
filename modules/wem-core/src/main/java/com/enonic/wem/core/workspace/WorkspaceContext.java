package com.enonic.wem.core.workspace;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public class WorkspaceContext
{
    private final Repository repository;

    private final Workspace workspace;

    private WorkspaceContext( final Repository repository, final Workspace workspace )
    {
        this.repository = repository;
        this.workspace = workspace;
    }

    public static WorkspaceContext from( final Context context )
    {
        return new WorkspaceContext( context.getRepository(), context.getWorkspace() );
    }

    public static WorkspaceContext from( final Workspace workspace, final Repository repository )
    {
        return new WorkspaceContext( repository, workspace );
    }

    public Repository getRepository()
    {
        return repository;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }


}
