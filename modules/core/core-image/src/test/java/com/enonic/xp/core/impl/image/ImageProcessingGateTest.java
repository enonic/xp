package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.exception.ThrottlingException;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessingGateTest
{
    @Test
    void duplicateWaiterDoesNotConsumeTheSecondProcessingSlot() throws Exception
    {
        final var gate = new ImageProcessingGate( 2, 2, 5 );
        final var started = new CountDownLatch( 1 );
        final var release = new CountDownLatch( 1 );
        final var calls = new AtomicInteger();
        final var duplicateThread = new AtomicReference<Thread>();
        final var duplicateEntering = new CountDownLatch( 1 );
        final ByteSource result = ByteSource.wrap( new byte[]{1} );
        try (var executor = Executors.newFixedThreadPool( 3 ))
        {
            final var first = executor.submit( () -> gate.execute( "same", () -> {
                calls.incrementAndGet(); started.countDown();
                assertTrue( release.await( 5, TimeUnit.SECONDS ) );
                return result;
            } ) );
            assertTrue( started.await( 5, TimeUnit.SECONDS ) );
            final var duplicate = executor.submit( () -> {
                duplicateThread.set( Thread.currentThread() );
                duplicateEntering.countDown();
                return gate.execute( "same", () -> { fail( "Duplicate computation" ); return result; } );
            } );
            try
            {
                assertTrue( duplicateEntering.await( 2, TimeUnit.SECONDS ) );
                assertTimeoutPreemptively( Duration.ofSeconds( 2 ), () -> {
                    while ( duplicateThread.get().getState() != Thread.State.WAITING ) { Thread.sleep( 1 ); }
                } );
                assertSame( result, executor.submit( () -> gate.execute( "other", () -> result ) ).get( 2, TimeUnit.SECONDS ) );
                assertFalse( first.isDone() );
            }
            finally { release.countDown(); }
            assertSame( result, first.get( 5, TimeUnit.SECONDS ) );
            assertSame( result, duplicate.get( 5, TimeUnit.SECONDS ) );
            assertEquals( 1, calls.get() );
        }
    }

    @Test
    void followerWaitsForRunningConversionBeyondQueueTimeout() throws Exception
    {
        final var gate = new ImageProcessingGate( 2, 2, 1 );
        final var started = new CountDownLatch( 1 );
        final var release = new CountDownLatch( 1 );
        final ByteSource result = ByteSource.wrap( new byte[]{42} );
        try (var executor = Executors.newFixedThreadPool( 2 ))
        {
            final var leader = executor.submit( () -> gate.execute( "same", () -> {
                started.countDown();
                assertTrue( release.await( 5, TimeUnit.SECONDS ) );
                return result;
            } ) );
            assertTrue( started.await( 2, TimeUnit.SECONDS ) );
            final var follower = executor.submit( () -> gate.execute( "same", () -> {
                fail( "Duplicate conversion" );
                return result;
            } ) );
            try
            {
                assertThrows( TimeoutException.class, () -> follower.get( 1500, TimeUnit.MILLISECONDS ) );
                assertFalse( leader.isDone() );
            }
            finally { release.countDown(); }
            assertSame( result, leader.get( 2, TimeUnit.SECONDS ) );
            assertSame( result, follower.get( 2, TimeUnit.SECONDS ) );
        }
    }

    @Test
    void interruptedFollowerReleasesCapacityWithoutCancellingLeader() throws Exception
    {
        final var gate = new ImageProcessingGate( 2, 0, 1 );
        final var started = new CountDownLatch( 1 );
        final var release = new CountDownLatch( 1 );
        final var followerThread = new AtomicReference<Thread>();
        final ByteSource result = ByteSource.wrap( new byte[]{42} );
        try (var executor = Executors.newFixedThreadPool( 2 ))
        {
            final var leader = executor.submit( () -> gate.execute( "same", () -> {
                started.countDown();
                assertTrue( release.await( 5, TimeUnit.SECONDS ) );
                return result;
            } ) );
            assertTrue( started.await( 2, TimeUnit.SECONDS ) );
            final var follower = executor.submit( () -> {
                followerThread.set( Thread.currentThread() );
                assertThrows( IOException.class, () -> gate.execute( "same", () -> result ) );
                return Thread.interrupted();
            } );
            try
            {
                assertTimeoutPreemptively( Duration.ofSeconds( 2 ), () -> {
                    while ( followerThread.get() == null || followerThread.get().getState() != Thread.State.WAITING )
                    {
                        Thread.sleep( 1 );
                    }
                } );
                followerThread.get().interrupt();
                assertTrue( follower.get( 2, TimeUnit.SECONDS ) );
                assertSame( result, gate.execute( "other", () -> result ) );
                assertFalse( leader.isDone() );
            }
            finally { release.countDown(); }
            assertSame( result, leader.get( 2, TimeUnit.SECONDS ) );
        }
    }

    @Test
    void failureRemovesPendingResultAndReleasesCapacity() throws Exception
    {
        final var gate = new ImageProcessingGate( 1, 0, 1 );
        assertThrows( IOException.class, () -> gate.execute( "key", () -> { throw new IOException( "failed" ); } ) );
        final ByteSource result = ByteSource.wrap( new byte[]{1} );
        assertSame( result, gate.execute( "key", () -> result ) );
    }

    @Test
    void excessRequestsAreRejectedAndInterruptedLeaderReleasesCapacity() throws Exception
    {
        final var gate = new ImageProcessingGate( 1, 0, 1 );
        assertThrows( IOException.class, () -> gate.execute( "first", () -> {
            assertThrows( ThrottlingException.class, () -> gate.execute( "second", ByteSource::empty ) );
            throw new InterruptedException();
        } ) );
        assertTrue( Thread.interrupted() );
        assertNotNull( gate.execute( "first", ByteSource::empty ) );
    }
}
