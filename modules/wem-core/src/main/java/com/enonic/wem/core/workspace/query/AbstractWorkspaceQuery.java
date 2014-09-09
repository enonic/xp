package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public abstract class AbstractWorkspaceQuery
{
    private final Workspace workspace;

    private final Repository repository;

    protected AbstractWorkspaceQuery( final Builder builder )
    {
        this.workspace = builder.workspace;
        this.repository = builder.repository;
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

    protected static class Builder<B extends Builder>
    {
        private Workspace workspace;

        private Repository repository;

        @SuppressWarnings("unchecked")
        public B workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B repository( final Repository repository )
        {
            this.repository = repository;
            return (B) this;
        }


    }

}
