package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImmutableFilesHelperTest
{
    @TempDir
    public Path temporaryFolder;

    private int supplierCall;

    @Test
    void concurrentMissesProduceOneFile()
        throws Exception
    {
        final ImmutableFilesHelper helper = new ImmutableFilesHelper( temporaryFolder.resolve( "tmp" ) );
        final Path path = temporaryFolder.resolve( "cached" );
        final CountDownLatch writing = new CountDownLatch( 1 );
        final CountDownLatch release = new CountDownLatch( 1 );
        final CountDownLatch secondStarted = new CountDownLatch( 1 );
        final AtomicReference<Thread> secondThread = new AtomicReference<>();
        final AtomicInteger conversions = new AtomicInteger();
        final Consumer<ByteSink> writer = sink -> {
            conversions.incrementAndGet();
            writing.countDown();
            try
            {
                assertTrue( release.await( 10, TimeUnit.SECONDS ) );
                sink.write( new byte[]{42} );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException( e );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        };
        try (var executor = Executors.newFixedThreadPool( 2 ))
        {
            final var first = executor.submit( () -> helper.computeIfAbsent( path, writer ) );
            assertTrue( writing.await( 5, TimeUnit.SECONDS ) );
            final var second = executor.submit( () -> {
                secondThread.set( Thread.currentThread() );
                secondStarted.countDown();
                return helper.computeIfAbsent( path, writer );
            } );
            try
            {
                assertTrue( secondStarted.await( 5, TimeUnit.SECONDS ) );
                final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 5 );
                while ( secondThread.get().getState() != Thread.State.WAITING && System.nanoTime() < deadline )
                {
                    Thread.sleep( 1 );
                }
                assertEquals( Thread.State.WAITING, secondThread.get().getState() );
            }
            finally
            {
                release.countDown();
            }
            assertTrue( first.get( 5, TimeUnit.SECONDS ).contentEquals( second.get( 5, TimeUnit.SECONDS ) ) );
            assertEquals( 1, conversions.get() );
        }
    }


    @Test
    void test_computeIfAbsent()
        throws Exception
    {
        final ImmutableFilesHelper immutableFilesHelper = new ImmutableFilesHelper( temporaryFolder );

        supplierCall = 0;

        final byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        final ByteSource source = ByteSource.wrap( bytes );
        Path path = temporaryFolder.resolve( "file.txt" );

        Consumer<ByteSink> consumer = sink -> {
            supplierCall++;
            try
            {
                sink.write( bytes );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        };

        ByteSource byteSource = immutableFilesHelper.computeIfAbsent( path, consumer );
        assertEquals( 1, supplierCall );
        assertTrue( source.contentEquals( byteSource ) );

        byteSource = immutableFilesHelper.computeIfAbsent( path, consumer );
        assertEquals( 1, supplierCall );
        assertTrue( source.contentEquals( byteSource ) );
    }
}
