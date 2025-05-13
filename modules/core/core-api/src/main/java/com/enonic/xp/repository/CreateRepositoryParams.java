package com.enonic.xp.repository;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.acl.AccessControlList;

public final class CreateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final PropertyTree data;

    private final AccessControlList rootPermissions;

    private final ChildOrder rootChildOrder;

    private final boolean transientFlag;

    private CreateRepositoryParams( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        rootPermissions = builder.rootPermissions;
        data = builder.data;
        rootChildOrder = builder.rootChildOrder;
        transientFlag = builder.transientFlag;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public AccessControlList getRootPermissions()
    {
        return rootPermissions;
    }

    public ChildOrder getRootChildOrder()
    {
        return rootChildOrder;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean isTransient()
    {
        return transientFlag;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private PropertyTree data;

        private AccessControlList rootPermissions = RepositoryConstants.DEFAULT_REPO_PERMISSIONS;

        private ChildOrder rootChildOrder = RepositoryConstants.DEFAULT_CHILD_ORDER;

        private boolean transientFlag;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder rootPermissions( final AccessControlList rootPermissions )
        {
            if ( rootPermissions != null )
            {
                this.rootPermissions = rootPermissions;
            }
            return this;
        }

        public Builder rootChildOrder( final ChildOrder rootChildOrder )
        {
            if ( rootChildOrder != null )
            {
                this.rootChildOrder = rootChildOrder;
            }
            return this;
        }

        public Builder transientFlag( final boolean value )
        {
            this.transientFlag = value;
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
