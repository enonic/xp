package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.util.concurrent.Striped;

public class ImmutableFilesHelper
{
    private static final Striped<Lock> FILE_LOCKS = Striped.lazyWeakLock( 100 );

    public static ByteSource computeIfAbsent( Path path, Function<ByteSink, Boolean> consumer )
        throws IOException
    {
        Objects.requireNonNull( path, "path is required" );
        Objects.requireNonNull( consumer, "consumer is required" );

        final Lock lock = FILE_LOCKS.get( path );
        lock.lock();

        try
        {
            if ( !Files.exists( path ) )
            {
                Files.createDirectories( path.getParent() );

                Boolean written = false;
                try
                {
                    written = consumer.apply( MoreFiles.asByteSink( path ) );
                    if ( !Boolean.TRUE.equals( written ) )
                    {
                        return null;
                    }
                }
                catch ( UncheckedIOException e )
                {
                    throw e.getCause();
                }
                finally
                {
                    if ( !Boolean.TRUE.equals( written ) )
                    {
                        Files.deleteIfExists( path );
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }

        return MoreFiles.asByteSource( path );
    }
}
