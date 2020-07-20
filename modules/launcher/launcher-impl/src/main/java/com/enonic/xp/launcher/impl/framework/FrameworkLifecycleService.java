package com.enonic.xp.launcher.impl.framework;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;

public final class FrameworkLifecycleService
    implements IntConsumer
{
    private final IntConsumer consumer;

    private final Executor asyncExecutor;

    public FrameworkLifecycleService( IntConsumer consumer, Executor asyncExecutor )
    {
        this.consumer = consumer;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void accept( final int value )
    {
        CompletableFuture.completedFuture( value ).thenAcceptAsync( consumer::accept, asyncExecutor );
    }
}
