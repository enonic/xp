package com.enonic.xp.context;

import java.util.concurrent.Callable;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.branch.Branch;

public interface Context
    extends ScopeAttributes
{
    public RepositoryId getRepositoryId();

    public Branch getBranch();

    public AuthenticationInfo getAuthInfo();

    public void runWith( Runnable runnable );

    public <T> T callWith( Callable<T> runnable );

    public LocalScope getLocalScope();
}
