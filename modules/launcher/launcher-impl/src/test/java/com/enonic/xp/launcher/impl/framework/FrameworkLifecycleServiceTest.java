package com.enonic.xp.launcher.impl.framework;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FrameworkLifecycleServiceTest
{
    @Mock
    IntConsumer consumer;

    @Test
    void singleCall()
        throws Exception
    {
        int rnd = ThreadLocalRandom.current().nextInt();

        final FrameworkLifecycleService frameworkLifecycleService = new FrameworkLifecycleService( consumer, Runnable::run );

        frameworkLifecycleService.accept( rnd );

        verify( consumer ).accept( rnd );
    }

    @Test
    void multipleCalls()
    {
        AtomicReference<CompletableFuture<Integer>> ref = new AtomicReference<>();

        final FrameworkLifecycleService frameworkLifecycleService = new FrameworkLifecycleService( consumer, Runnable::run );

        frameworkLifecycleService.accept( 1 );
        frameworkLifecycleService.accept( 2 );

        final InOrder inOrder = inOrder( consumer );
        inOrder.verify( consumer ).accept( 1 );
        inOrder.verify( consumer ).accept( 2 );
    }
}