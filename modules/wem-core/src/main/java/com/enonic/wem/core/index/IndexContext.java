package com.enonic.wem.core.index;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public class IndexContext
{
    private Repository repository;

    private Workspace workspace;

    private IndexContext( final Repository repository, final Workspace workspace )
    {
        this.repository = repository;
        this.workspace = workspace;
    }

    public static IndexContext from( final Context context )
    {
        return new IndexContext( context.getRepository(), context.getWorkspace() );
    }

    public static IndexContext from( final Workspace workspace, final Repository repository )
    {
        return new IndexContext( repository, workspace );
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
