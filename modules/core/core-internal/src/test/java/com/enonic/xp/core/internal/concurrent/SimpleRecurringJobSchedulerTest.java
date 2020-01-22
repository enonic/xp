package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleRecurringJobSchedulerTest
{
    private static final Duration DELAY = Duration.ofMillis( 1 );

    private SimpleRecurringJobScheduler simpleRecurringJobScheduler;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @BeforeEach
    void setUp()
    {
        final Function<ThreadFactory, ScheduledExecutorService> newSingleThreadScheduledExecutor = this::wrappedScheduledExecutorService;
        simpleRecurringJobScheduler = new SimpleRecurringJobScheduler( newSingleThreadScheduledExecutor, "test-thread" );
    }

    @AfterEach
    void tearDown()
    {
        final List<Runnable> runnables = simpleRecurringJobScheduler.shutdownNow();
        assertNotNull( runnables );
    }

    @Test
    void scheduleWithFixedDelay_exception_does_not_stop_scheduling()
    {
        final AtomicReference<Throwable> unexpectedThrowable = new AtomicReference<>();
        final Phaser phaser = new Phaser( 2 );
        simpleRecurringJobScheduler.scheduleWithFixedDelay( () -> {
            throw new RuntimeException( "Intentional exception" );
        }, Duration.ZERO, DELAY, e -> phaser.arriveAndAwaitAdvance(), unexpectedThrowable::set );
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();
        assertTrue( phaser.getPhase() >= 2, "At least two exceptions should have been counted" );
        assertNull( unexpectedThrowable.get(), "Throwable is not expected" );
    }

    @Test
    void scheduleWithFixedDelay_error_stops_scheduling()
    {
        final AtomicReference<Throwable> unexpectedException = new AtomicReference<>();
        final Phaser phaser = new Phaser( 2 );

        simpleRecurringJobScheduler.scheduleWithFixedDelay( () -> {
            throw new Error( "Intentional error" );
        }, Duration.ZERO, DELAY, unexpectedException::set, e -> phaser.arriveAndAwaitAdvance() );

        phaser.arriveAndAwaitAdvance();

        assertTrue( scheduledThreadPoolExecutor.getQueue().isEmpty() );
        assertNull( unexpectedException.get(), "Exception is not expected" );
    }

    @Test
    void scheduleWithFixedDelay_cancel_stops_scheduling()
    {
        final AtomicReference<Throwable> unexpectedException = new AtomicReference<>();
        final Phaser phaser = new Phaser( 2 );

        final RecurringJob recurringJob =
            simpleRecurringJobScheduler.scheduleWithFixedDelay( phaser::arriveAndAwaitAdvance, Duration.ZERO, DELAY,
                                                                unexpectedException::set, unexpectedException::set );

        phaser.arriveAndAwaitAdvance();
        recurringJob.cancel();

        final List<Runnable> runnables = List.copyOf( scheduledThreadPoolExecutor.getQueue() );
        assertTrue( runnables.isEmpty() || ( (ScheduledFuture<?>) runnables.get( 0 ) ).isDone() );
        assertNull( unexpectedException.get(), "Exception is not expected" );
    }

    private ScheduledExecutorService wrappedScheduledExecutorService( ThreadFactory threadFactory )
    {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor( 1, threadFactory );
        return scheduledThreadPoolExecutor;
    }
}