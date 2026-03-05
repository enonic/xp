package com.enonic.xp.context;

import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;


public interface Context
    extends ScopeAttributes
{
    RepositoryId getRepositoryId();

    Branch getBranch();

    @NonNull AuthenticationInfo getAuthInfo();

    void runWith( Runnable runnable );

    <T> T callWith( Callable<T> runnable );

    LocalScope getLocalScope();
}
