package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.io.ByteSource;

import com.enonic.xp.exception.ThrottlingException;

/** Bounded request admission; only the leader for a rendition acquires a processing slot. */
final class ImageProcessingGate
{
    private final Semaphore requests;
    private final Semaphore slots;
    private final int timeoutSeconds;
    private final ConcurrentHashMap<Object, CompletableFuture<ByteSource>> pending = new ConcurrentHashMap<>();

    ImageProcessingGate( final int concurrent, final int queued, final int timeoutSeconds )
    {
        requests = new Semaphore( Math.addExact( concurrent, queued ) );
        slots = new Semaphore( concurrent, true );
        this.timeoutSeconds = timeoutSeconds;
    }

    ByteSource execute( final Object key, final Callable<ByteSource> operation ) throws IOException
    {
        if ( !requests.tryAcquire() ) { throw new ThrottlingException( "Image processing queue is full" ); }
        final var result = new CompletableFuture<ByteSource>();
        final var existing = pending.putIfAbsent( key, result );
        try
        {
            if ( existing != null )
            {
                try { return existing.get( timeoutSeconds, TimeUnit.SECONDS ); }
                catch ( ExecutionException e ) { return rethrow( e.getCause() ); }
                catch ( TimeoutException e ) { throw new ThrottlingException( "Image processing queue timed out" ); }
            }
            try
            {
                if ( !slots.tryAcquire( timeoutSeconds, TimeUnit.SECONDS ) )
                {
                    throw new ThrottlingException( "Image processing queue timed out" );
                }
                try
                {
                    final ByteSource value = operation.call();
                    result.complete( value );
                    return value;
                }
                finally { slots.release(); }
            }
            catch ( Throwable e )
            {
                result.completeExceptionally( e );
                if ( e instanceof InterruptedException interrupted ) { throw interrupted; }
                return rethrow( e );
            }
            finally { pending.remove( key, result ); }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IOException( "Interrupted waiting for image processing", e );
        }
        finally { requests.release(); }
    }

    private static ByteSource rethrow( final Throwable cause ) throws IOException
    {
        if ( cause instanceof IOException io ) { throw io; }
        if ( cause instanceof RuntimeException runtime ) { throw runtime; }
        if ( cause instanceof Error error ) { throw error; }
        throw new IOException( "Image processing failed", cause );
    }
}
