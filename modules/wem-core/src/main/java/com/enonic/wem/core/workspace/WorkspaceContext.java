package com.enonic.wem.core.workspace;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.workspace.Workspace;

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

        if ( repository != null ? !repository.equals( that.repository ) : that.repository != null )
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
        int result = repository != null ? repository.hashCode() : 0;
        result = 31 * result + ( workspace != null ? workspace.hashCode() : 0 );
        return result;
    }
}
