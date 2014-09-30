package com.enonic.wem.api.repository;

import com.enonic.wem.api.workspace.Workspaces;

public class Repository
{
    private final RepositoryId id;

    private final Workspaces workspaces;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Repository ) )
        {
            return false;
        }

        final Repository that = (Repository) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }
        if ( workspaces != null ? !workspaces.equals( that.workspaces ) : that.workspaces != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( workspaces != null ? workspaces.hashCode() : 0 );
        return result;
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
