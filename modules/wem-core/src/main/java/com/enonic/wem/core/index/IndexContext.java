package com.enonic.wem.core.index;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.Context2;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class IndexContext
{
    private final RepositoryId repositoryId;

    private final Workspace workspace;

    private IndexContext( final RepositoryId repositoryId, final Workspace workspace )
    {
        this.repositoryId = repositoryId;
        this.workspace = workspace;
    }

    public static IndexContext from( final Context context )
    {
        return new IndexContext( context.getRepositoryId(), context.getWorkspace() );
    }

    public static IndexContext from( final Context2 context )
    {
        return new IndexContext( context.getRepositoryId(), context.getWorkspace() );
    }


    public static IndexContext from( final Workspace workspace, final RepositoryId repositoryId )
    {
        return new IndexContext( repositoryId, workspace );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
