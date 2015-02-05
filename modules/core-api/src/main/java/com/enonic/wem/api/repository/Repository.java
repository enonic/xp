package com.enonic.wem.api.repository;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;

public class Repository
{
    private final RepositoryId id;

    private final Workspaces workspaces;

    private Repository( Builder builder )
    {
        this.workspaces = builder.workspaces;
        this.id = builder.id;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public Workspaces getWorkspaces()
    {
        return workspaces;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Repository that = (Repository) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static final class Builder
    {
        private RepositoryId id;

        private Workspaces workspaces;

        private Builder()
        {
        }

        public Builder workspaces( final Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public Builder workspaces( final Workspace... workspaces )
        {
            this.workspaces = Workspaces.from( workspaces );
            return this;
        }

        public Builder id( RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Repository build()
        {
            return new Repository( this );
        }
    }
}
