package com.enonic.xp.impl.task.distributed;

import java.io.Serializable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class TaskContext
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final Branch branch;

    private final RepositoryId repo;

    private final AuthenticationInfo authInfo;

    private final NodePath contentRootPath;

    private TaskContext( final Builder builder )
    {
        this.branch = builder.branch;
        this.repo = builder.repo;
        this.authInfo = builder.authInfo;
        this.contentRootPath = builder.contentRootPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Branch getBranch()
    {
        return branch;
    }

    public RepositoryId getRepo()
    {
        return repo;
    }

    public AuthenticationInfo getAuthInfo()
    {
        return authInfo;
    }

    public NodePath getContentRootPath()
    {
        return contentRootPath;
    }

    public static final class Builder
    {
        private Branch branch;

        private RepositoryId repo;

        private AuthenticationInfo authInfo;

        private NodePath contentRootPath;

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setRepo( final RepositoryId repo )
        {
            this.repo = repo;
            return this;
        }

        public Builder setAuthInfo( final AuthenticationInfo authInfo )
        {
            this.authInfo = authInfo;
            return this;
        }

        public Builder setContentRootPath( final NodePath contentRootPath )
        {
            this.contentRootPath = contentRootPath;
            return this;
        }

        public TaskContext build()
        {
            return new TaskContext( this );
        }

    }
}
