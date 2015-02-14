package com.enonic.xp.core.context;

import java.util.concurrent.Callable;

import com.enonic.xp.core.repository.RepositoryId;
import com.enonic.xp.core.security.auth.AuthenticationInfo;
import com.enonic.xp.core.branch.Branch;

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
