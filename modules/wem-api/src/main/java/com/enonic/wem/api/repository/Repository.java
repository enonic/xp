package com.enonic.wem.api.repository;

import com.enonic.wem.api.entity.Workspaces;

public class Repository
{
    private RepositoryId id;

    private Workspaces workspaces;

    private Repository( Builder builder )
    {
        id = builder.id;
        workspaces = builder.workspaces;
    }

    public Workspaces getWorkspaces()
    {
        return workspaces;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private RepositoryId id;

        private Workspaces workspaces;

        private Builder()
        {
        }

        public Builder id( RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Builder workspaces( Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public Repository build()
        {
            return new Repository( this );
        }
    }
}
