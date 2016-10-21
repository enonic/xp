package com.enonic.xp.repository;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.acl.AccessControlList;

public class CreateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final Branches branches;

    private final RepositorySettings repositorySettings;

    private final AccessControlList rootPermissions;

    private final boolean inheritPermissions;

    private final ChildOrder rootChildOrder;

    private CreateRepositoryParams( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        branches = builder.branches == null ? Branches.from( RepositoryConstants.MASTER_BRANCH ) : builder.branches;
        repositorySettings = builder.repositorySettings == null ? RepositorySettings.create().build() : builder.repositorySettings;
        rootPermissions = builder.rootPermissions;
        inheritPermissions = builder.inheritPermissions;
        rootChildOrder = builder.rootChildOrder;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public RepositorySettings getRepositorySettings()
    {
        return repositorySettings;
    }

    public AccessControlList getRootPermissions()
    {
        return rootPermissions;
    }

    public ChildOrder getRootChildOrder()
    {
        return rootChildOrder;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Branches branches;

        private RepositorySettings repositorySettings;

        private AccessControlList rootPermissions = RepositoryConstants.DEFAULT_REPO_PERMISSIONS;

        private boolean inheritPermissions = true;

        private ChildOrder rootChildOrder = RepositoryConstants.DEFAULT_CHILD_ORDER;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder repositorySettings( final RepositorySettings repositorySettings )
        {
            this.repositorySettings = repositorySettings;
            return this;
        }

        public Builder rootPermissions( final AccessControlList val )
        {
            rootPermissions = val;
            return this;
        }

        public Builder inheritPermissions( final boolean val )
        {
            inheritPermissions = val;
            return this;
        }

        public Builder rootChildOrder( final ChildOrder val )
        {
            rootChildOrder = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId cannot be null" );
        }

        public CreateRepositoryParams build()
        {
            validate();
            return new CreateRepositoryParams( this );
        }
    }
}
