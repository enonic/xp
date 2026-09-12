package com.enonic.xp.core.impl.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.*;

class PreparedImageSourceTest
{
    @TempDir Path folder;

    @Test
    void hashesAndCopiesTheSameSingleRead() throws Exception
    {
        final AtomicInteger opens = new AtomicInteger();
        final ByteSource source = new ByteSource()
        {
            @Override public InputStream openStream()
            {
                assertEquals( 1, opens.incrementAndGet() );
                return new ByteArrayInputStream( new byte[]{1, 2, 3} );
            }
        };
        try (var prepared = PreparedImageSource.copy( source, folder, 3, null ))
        {
            assertArrayEquals( new byte[]{1, 2, 3}, Files.readAllBytes( prepared.path() ) );
            assertEquals( 128, prepared.checksum().length() );
        }
        assertEmpty();
    }

    @Test
    void oversizedStreamIsNotReadToItsEnd() throws Exception
    {
        final AtomicInteger bytes = new AtomicInteger();
        final ByteSource source = new ByteSource()
        {
            @Override public InputStream openStream()
            {
                return new InputStream()
                {
                    @Override public int read()
                    {
                        assertTrue( bytes.incrementAndGet() <= 9, "Read past the configured bound" );
                        return 0;
                    }
                };
            }
        };
        assertThrows( IllegalArgumentException.class, () -> PreparedImageSource.copy( source, folder, 8, null ) );
        assertEquals( 9, bytes.get() );
        assertEmpty();
    }

    @Test
    void checksumMismatchAndReadFailureRemoveTemporaryFile() throws Exception
    {
        assertThrows( IllegalStateException.class, () -> PreparedImageSource.copy( ByteSource.wrap( new byte[]{1} ), folder, 8, "wrong" ) );
        assertEmpty();
        assertThrows( IOException.class, () -> PreparedImageSource.copy( new ByteSource()
        {
            @Override public InputStream openStream() throws IOException { throw new IOException( "unavailable" ); }
        }, folder, 8, null ) );
        assertEmpty();
    }

    private void assertEmpty() throws IOException
    {
        try (var files = Files.list( folder )) { assertEquals( 0, files.count() ); }
    }
}
