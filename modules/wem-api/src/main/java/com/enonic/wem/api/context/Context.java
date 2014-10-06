package com.enonic.wem.api.context;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class Context
{
    private final Workspace workspace;

    private RepositoryId repositoryId;

    private Context( Builder builder )
    {
        workspace = builder.workspace;
        repositoryId = builder.repositoryId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Context ) )
        {
            return false;
        }

        final Context context = (Context) o;

        if ( workspace != null ? !workspace.equals( context.workspace ) : context.workspace != null )
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

    public static final class Builder
    {
        private Workspace workspace;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Context build()
        {
            return new Context( this );
        }
    }
}
