package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.export.ExportNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipExportWriterTest
{
    @TempDir
    Path tempDir;

    /**
     * Verifies that every local file header in the zip has bit 3 of the general purpose bit flag set,
     * which means the entry is followed by a data descriptor. This is the byte-level proof that
     * {@link org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream} was constructed with an
     * {@link java.io.OutputStream} (sequential mode) rather than a {@link java.nio.channels.SeekableByteChannel}
     * (random-access mode where sizes/CRC are back-patched into the LFH after the data is written).
     */
    private static void assertAllLocalHeadersUseDataDescriptor( final Path zipPath )
        throws IOException
    {
        final byte[] bytes = Files.readAllBytes( zipPath );
        int checked = 0;
        try (ZipFile zf = ZipFile.builder().setPath( zipPath ).get())
        {
            final Enumeration<ZipArchiveEntry> entries = zf.getEntries();
            while ( entries.hasMoreElements() )
            {
                final ZipArchiveEntry entry = entries.nextElement();
                final int off = (int) entry.getLocalHeaderOffset();
                // LFH signature 0x04034b50 (little-endian: 50 4b 03 04)
                assertEquals( 0x50, bytes[off] & 0xFF, "LFH signature mismatch for " + entry.getName() );
                assertEquals( 0x4b, bytes[off + 1] & 0xFF );
                assertEquals( 0x03, bytes[off + 2] & 0xFF );
                assertEquals( 0x04, bytes[off + 3] & 0xFF );
                // GP flag at offset+6, 2 bytes little-endian; bit 3 (0x0008) = data descriptor follows
                final int gpFlag = ( bytes[off + 6] & 0xFF ) | ( ( bytes[off + 7] & 0xFF ) << 8 );
                assertTrue( ( gpFlag & 0x0008 ) != 0,
                            "Entry [" + entry.getName() + "] should have data descriptor flag (bit 3) set in LFH; " +
                                "actual GP flag = 0x" + Integer.toHexString( gpFlag ) );
                checked++;
            }
        }
        assertTrue( checked > 0, "Expected at least one entry to verify" );
    }

    @Test
    void writesElementAndSourceIntoZip()
        throws IOException
    {
        final String exportName = "my-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "node.xml" ), "<node/>" );
            writer.writeSource( baseDir.resolve( "bin" ).resolve( "blob.dat" ),
                                ByteSource.wrap( "payload".getBytes( StandardCharsets.UTF_8 ) ) );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should be created" );

        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertEquals( "<node/>", Files.readString( fs.getPath( "/my-export/node.xml" ), StandardCharsets.UTF_8 ) );
            assertEquals( "payload", new String( Files.readAllBytes( fs.getPath( "/my-export/bin/blob.dat" ) ), StandardCharsets.UTF_8 ) );
        }
    }

    @Test
    void writesSequentially_localFileHeadersUseDataDescriptor()
        throws IOException
    {
        final String exportName = "sequential-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "a.xml" ), "first" );
            writer.writeSource( baseDir.resolve( "b.bin" ), ByteSource.wrap( "second".getBytes( StandardCharsets.UTF_8 ) ) );
        }

        assertAllLocalHeadersUseDataDescriptor( tempDir.resolve( exportName + ".zip" ) );
    }

    @Test
    void rejectsItemPathOutsideBaseDirectory()
        throws IOException
    {
        final String exportName = "guarded-export";

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            final Path outside = tempDir.resolve( "elsewhere" ).resolve( "evil.xml" );
            assertThrows( ExportNodeException.class, () -> writer.writeElement( outside, "nope" ) );
        }
    }

    @Test
    void rejectsUnsafeExportName()
    {
        assertThrows( IllegalArgumentException.class, () -> ZipExportWriter.create( tempDir, "../escape" ) );
    }

    @Test
    void closeIsIdempotent()
        throws IOException
    {
        final String exportName = "closeable-export";
        final ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName );
        writer.writeElement( tempDir.resolve( exportName ).resolve( "x.xml" ), "x" );
        writer.close();
        writer.close();
    }
}
