package com.enonic.wem.repo.internal.index;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.workspace.Workspace;

public class IndexContext
{
    private final RepositoryId repositoryId;

    private final Workspace workspace;

    private final Principals principals;

    private IndexContext( Builder builder )
    {
        repositoryId = builder.repositoryId;
        workspace = builder.workspace;
        principals = builder.principals;
    }

    public static IndexContext from( final Context context )
    {
        return IndexContext.create().
            workspace( context.getWorkspace() ).
            repositoryId( context.getRepositoryId() ).
            principals( Principals.empty() ).
            build();
    }

    public static IndexContext from( final Workspace workspace, final RepositoryId repositoryId )
    {
        return IndexContext.create().
            workspace( workspace ).
            repositoryId( repositoryId ).
            principals( Principals.empty() ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public Principals getPrincipals()
    {
        return principals;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Workspace workspace;

        private Principals principals;

        private Builder()
        {
        }

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder principals( Principals principals )
        {
            this.principals = principals;
            return this;
        }

        public IndexContext build()
        {
            return new IndexContext( this );
        }
    }
}
