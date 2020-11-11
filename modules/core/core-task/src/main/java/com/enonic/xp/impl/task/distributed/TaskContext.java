package com.enonic.xp.impl.task.distributed;

import java.io.Serializable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class TaskContext
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final Branch branch;

    private final RepositoryId repo;

    private final AuthenticationInfo authInfo;

    public TaskContext( final Branch branch, final RepositoryId repo, final AuthenticationInfo authInfo )
    {
        this.branch = branch;
        this.repo = repo;
        this.authInfo = authInfo;
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
}
