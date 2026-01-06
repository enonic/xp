package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SimpleExecutorTest
{
    @Test
    void testExecute()
    {
        final AtomicReference<Throwable> unexpectedThrowable = new AtomicReference<>();
        final SimpleExecutor simpleExecutor = SimpleExecutor.ofSingle( "test-thread", unexpectedThrowable::set );
        try
        {
            final Phaser phaser = new Phaser( 2 );

            final Runnable mock = mock( Runnable.class );
            simpleExecutor.execute( mock );
            simpleExecutor.execute( phaser::arriveAndAwaitAdvance );

            phaser.arriveAndAwaitAdvance();
            verify( mock ).run();
            assertNull( unexpectedThrowable.get(), "Throwable is not expected" );
        }
        finally
        {
            simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> {
            } );
        }
    }

    @Test
    void shutdownAndAwaitTermination_interrupt()
    {
        final AtomicReference<Throwable> unexpectedThrowable = new AtomicReference<>();
        final AtomicBoolean unexpectedExecution = new AtomicBoolean();

        final AtomicBoolean terminated = new AtomicBoolean();
        final AtomicBoolean interrupted = new AtomicBoolean();
        final Phaser phaser = new Phaser( 2 );
        final Phaser locker = new Phaser( 1 );

        final SimpleExecutor simpleExecutor = SimpleExecutor.ofSingle( "test-thread", unexpectedThrowable::set );

        final Thread thread = new Thread( () -> {

            simpleExecutor.execute( () -> {
                locker.awaitAdvance( 0 ); // locker will make execution to stuck forever
            } );
            simpleExecutor.execute( () -> unexpectedExecution.set( true ) );

            terminated.set( simpleExecutor.shutdownAndAwaitTermination( Duration.ofMinutes( 10 ), neverCommenced -> interrupted.set(
                Thread.currentThread().isInterrupted() ) ) );
            phaser.arriveAndAwaitAdvance();
        } );
        thread.start();
        thread.interrupt();
        phaser.arriveAndAwaitAdvance();
        assertFalse( terminated.get() );
        assertTrue( interrupted.get() );
        assertNull( unexpectedThrowable.get(), "Throwable is not expected" );
        assertFalse( unexpectedExecution.get(), "Second execution is not expected" );
    }

    @Test
    void testExecute_stuck()
    {
        final AtomicReference<Throwable> unexpectedThrowable = new AtomicReference<>();
        final AtomicBoolean unexpectedExecution = new AtomicBoolean();
        final SimpleExecutor simpleExecutor = SimpleExecutor.ofSingle( "test-thread", unexpectedThrowable::set );
        try
        {
            AtomicReference<List<Runnable>> neverCommencedCollector = new AtomicReference<>();
            final Phaser phaser = new Phaser( 2 );
            final Phaser locker = new Phaser( 1 );

            simpleExecutor.execute( () -> {
                phaser.arriveAndAwaitAdvance();
                locker.awaitAdvance( 0 ); // locker will make execution to stuck forever
            } );

            // will never be commenced because we use Executors::newSingleThreadExecutor
            simpleExecutor.execute( () -> unexpectedExecution.set( true ) );

            phaser.arriveAndAwaitAdvance();
            assertNull( unexpectedThrowable.get(), "Throwable is not expected" );
            assertFalse( unexpectedExecution.get(), "Second execution is not expected" );
            final boolean terminated = simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommencedCollector::set );
            assertFalse( terminated );
            assertEquals( 1, neverCommencedCollector.get().size() );
        }
        finally
        {
            simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> {
            } );
        }
    }

    @Test
    void testException()
    {
        final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        final Consumer<Throwable> mock = exceptionRef::set;

        final Phaser phaser = new Phaser( 2 );

        final SimpleExecutor simpleExecutor = SimpleExecutor.ofSingle( "test-thread", mock.andThen( e -> phaser.arriveAndAwaitAdvance() ) );
        try
        {
            final RuntimeException intendedException = new RuntimeException( "Intended Exception" );

            simpleExecutor.execute( () -> {
                throw intendedException;
            } );

            phaser.arriveAndAwaitAdvance();

            assertSame( intendedException, exceptionRef.get() );
        }
        finally
        {
            simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> {
            } );
        }
    }
}
