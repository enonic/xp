package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.Workspace;

public abstract class AbstractWorkspaceQuery
{
    private final Workspace workspace;

    protected AbstractWorkspaceQuery( final Workspace workspace )
    {
        this.workspace = workspace;
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
        if ( !( o instanceof AbstractWorkspaceQuery ) )
        {
            return false;
        }

        final AbstractWorkspaceQuery that = (AbstractWorkspaceQuery) o;

        if ( workspace != null ? !workspace.equals( that.workspace ) : that.workspace != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return workspace != null ? workspace.hashCode() : 0;
    }
}
