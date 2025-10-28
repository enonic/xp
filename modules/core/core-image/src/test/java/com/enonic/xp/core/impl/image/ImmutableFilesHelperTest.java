package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImmutableFilesHelperTest
{
    @TempDir
    public Path temporaryFolder;

    private int supplierCall;


    @Test
    void test_computeIfAbsent()
        throws Exception
    {
        supplierCall = 0;

        final byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        final ByteSource source = ByteSource.wrap( bytes );
        Path path = temporaryFolder.resolve( "file.txt" );

        Function<ByteSink, Boolean> consumer = sink -> {
            supplierCall++;
            try
            {
                sink.write( bytes );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
            return true;
        };

        ByteSource byteSource = ImmutableFilesHelper.computeIfAbsent( path, consumer );
        assertEquals( 1, supplierCall );
        assertTrue( source.contentEquals( byteSource ) );

        byteSource = ImmutableFilesHelper.computeIfAbsent( path, consumer );
        assertEquals( 1, supplierCall );
        assertTrue( source.contentEquals( byteSource ) );
    }

    @Test
    void test_incorrect_computeIfAbsent()
        throws Exception
    {
        Path path = temporaryFolder.resolve( "unknown_file.txt" );
        Function<ByteSink, Boolean> consumer = sink -> false;

        ByteSource byteSource = ImmutableFilesHelper.computeIfAbsent( path, consumer );
        assertNull( byteSource );
    }
}
