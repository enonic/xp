package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipDumpWriterV8Test
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
    void writesSequentially_localFileHeadersUseDataDescriptor()
        throws IOException
    {
        final String dumpName = "sequential-dump";

        try (ZipDumpWriterV8 writer = ZipDumpWriterV8.create( tempDir, dumpName ))
        {
            writer.openVersionsMeta( RepositoryId.from( "test-repo" ) );
            writer.writeRawEntry( "entry-1.json", "{\"a\":1}".getBytes( StandardCharsets.UTF_8 ) );
            writer.writeRawEntry( "entry-2.json", "{\"b\":2}".getBytes( StandardCharsets.UTF_8 ) );
            writer.closeMeta();
        }

        assertAllLocalHeadersUseDataDescriptor( tempDir.resolve( dumpName + ".zip" ) );
    }
}
