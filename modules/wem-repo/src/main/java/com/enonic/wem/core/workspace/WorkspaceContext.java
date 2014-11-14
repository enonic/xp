package com.enonic.wem.core.workspace;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class WorkspaceContext
{
    private final RepositoryId repositoryId;

    private final Workspace workspace;

    private WorkspaceContext( final RepositoryId repositoryId, final Workspace workspace )
    {
        this.repositoryId = repositoryId;
        this.workspace = workspace;
    }

    public static WorkspaceContext from( final Context context )
    {
        return new WorkspaceContext( context.getRepositoryId(), context.getWorkspace() );
    }


    public static WorkspaceContext from( final Workspace workspace, final RepositoryId repositoryId )
    {
        return new WorkspaceContext( repositoryId, workspace );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceContext ) )
        {
            return false;
        }

        final WorkspaceContext that = (WorkspaceContext) o;

        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }
        if ( workspace != null ? !workspace.equals( that.workspace ) : that.workspace != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( workspace != null ? workspace.hashCode() : 0 );
        return result;
    }
}
