package com.enonic.wem.repo.internal.entity;

import java.util.concurrent.Callable;

import com.enonic.xp.core.context.ContextAccessor;
import com.enonic.xp.core.context.ContextBuilder;

public class NodeHelper
{
    public static void runAsAdmin( final Runnable runnable )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
            build().
            runWith( runnable );
    }

    public static <T> T runAsAdmin( final Callable<T> callable )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
            build().
            callWith( callable );
    }
}
