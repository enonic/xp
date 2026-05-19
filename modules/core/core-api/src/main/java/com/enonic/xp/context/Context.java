package com.enonic.xp.context;

import java.util.concurrent.Callable;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;


@NullMarked
public interface Context
    extends ScopeAttributes
{
    @Nullable RepositoryId getRepositoryId();

    @Nullable Branch getBranch();

    AuthenticationInfo getAuthInfo();

    void runWith( Runnable runnable );

    <T extends @Nullable Object> T callWith( Callable<T> runnable );

    LocalScope getLocalScope();
}
