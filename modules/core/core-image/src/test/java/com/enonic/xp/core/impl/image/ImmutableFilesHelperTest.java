package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
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
