package com.enonic.xp.repo.impl.entity;

import java.util.concurrent.Callable;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;

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
