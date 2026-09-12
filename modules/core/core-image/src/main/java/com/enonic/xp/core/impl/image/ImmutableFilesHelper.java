package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.exception.ThrottlingException;

import static java.util.Objects.requireNonNull;

public class ImmutableFilesHelper
{
    private static final ConcurrentHashMap<Path, LockEntry> FILE_LOCKS = new ConcurrentHashMap<>();

    private static final class LockEntry
    {
        final Lock lock = new ReentrantLock();
        int users;
    }

    final Path tmpDir;

    public ImmutableFilesHelper( final Path tmpDir )
    {
        this.tmpDir = tmpDir;
    }

    public ByteSource computeIfAbsent( final Path path, final Consumer<ByteSink> consumer )
        throws IOException
    {
        return computeIfAbsent( path, consumer, 0 );
    }

    public ByteSource computeIfAbsent( final Path path, final Consumer<ByteSink> consumer, final int lockTimeoutSeconds )
        throws IOException
    {
        requireNonNull( path, "path is required" );
        requireNonNull( consumer, "consumer is required" );

        if ( Files.exists( path ) )
        {
            return MoreFiles.asByteSource( path );
        }

        final LockEntry entry = FILE_LOCKS.compute( path, ( key, current ) -> {
            final LockEntry result = current == null ? new LockEntry() : current;
            result.users++;
            return result;
        } );
        try
        {
            return computeLocked( path, consumer, lockTimeoutSeconds, entry.lock );
        }
        finally
        {
            FILE_LOCKS.compute( path, ( key, current ) -> --current.users == 0 ? null : current );
        }
    }

    private ByteSource computeLocked( final Path path, final Consumer<ByteSink> consumer, final int lockTimeoutSeconds,
                                      final Lock lock ) throws IOException
    {
        if ( lockTimeoutSeconds > 0 )
        {
            try
            {
                if ( !lock.tryLock( lockTimeoutSeconds, TimeUnit.SECONDS ) )
                {
                    throw new ThrottlingException( "Image cache is busy" );
                }
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new IOException( "Interrupted waiting for image cache", e );
            }
        }
        else
        {
            lock.lock();
        }
        try
        {
            // Another request may have populated the cache while this request waited.
            if ( Files.exists( path ) )
            {
                return MoreFiles.asByteSource( path );
            }
            Files.createDirectories( tmpDir );
            final Path tmpPath = Files.createTempFile( tmpDir, "img", null );
            try
            {
                try
                {
                    consumer.accept( MoreFiles.asByteSink( tmpPath ) );
                }
                catch ( UncheckedIOException e )
                {
                    throw e.getCause();
                }

                Files.createDirectories( path.getParent() );
                try
                {
                    try
                    {
                        Files.move( tmpPath, path, StandardCopyOption.ATOMIC_MOVE );
                    }
                    catch ( AtomicMoveNotSupportedException e )
                    {
                        Files.move( tmpPath, path );
                    }
                }
                catch ( FileAlreadyExistsException e )
                {
                    Files.deleteIfExists( tmpPath );
                }
                return MoreFiles.asByteSource( path );
            }
            catch ( Exception e )
            {
                Files.deleteIfExists( tmpPath );
                throw e;
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}
