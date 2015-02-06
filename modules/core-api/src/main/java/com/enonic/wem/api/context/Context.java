package com.enonic.wem.api.context;

import java.util.concurrent.Callable;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.branch.Branch;

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
