package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.util.concurrent.Striped;

import static java.util.Objects.requireNonNull;


public class ImmutableFilesHelper
{
    private static final Striped<Lock> FILE_LOCKS = Striped.lazyWeakLock( 100 );

    final Path tmpDir;

    public ImmutableFilesHelper( final Path tmpDir )
    {
        this.tmpDir = tmpDir;
    }

    public ByteSource computeIfAbsent( final Path path, final Consumer<ByteSink> consumer )
        throws IOException
    {
        requireNonNull( path, "path is required" );
        requireNonNull( consumer, "consumer is required" );

        if ( Files.exists( path ) )
        {
            return MoreFiles.asByteSource( path );
        }

        final Lock lock = FILE_LOCKS.get( path );
        lock.lock();
        try
        {
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
