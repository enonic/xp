package com.enonic.xp.context;

import java.util.concurrent.Callable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

@PublicApi
public interface Context
    extends ScopeAttributes
{
    RepositoryId getRepositoryId();

    Branch getBranch();

    AuthenticationInfo getAuthInfo();

    void runWith( Runnable runnable );

    <T> T callWith( Callable<T> runnable );

    LocalScope getLocalScope();
}
