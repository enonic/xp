package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipDumpWriterTest
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

        try (ZipDumpWriter writer = ZipDumpWriter.create( tempDir, dumpName, null ))
        {
            // openVersionsMeta -> openTarStream -> openMetaFileStream creates a single zip entry
            // wrapping a tar.gz; closeMeta closes that entry. No blobs are added so the null
            // BlobStore is never touched.
            writer.openVersionsMeta( RepositoryId.from( "test-repo" ) );
            writer.closeMeta();
            writer.writeDumpMetaData( dumpMeta() );
        }

        assertAllLocalHeadersUseDataDescriptor( tempDir.resolve( dumpName + ".zip" ) );
    }

    @Test
    void writesToPartialFileUntilMetaDataWrittenAndClosed()
        throws IOException
    {
        final String dumpName = "partial-dump";

        try (ZipDumpWriter writer = ZipDumpWriter.create( tempDir, dumpName, null ))
        {
            writer.writeDumpMetaData( dumpMeta() );
            assertTrue( Files.exists( tempDir.resolve( dumpName + ".zip.partial" ) ) );
            assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip" ) ) );
        }

        assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip.partial" ) ) );
        assertTrue( Files.exists( tempDir.resolve( dumpName + ".zip" ) ) );
    }

    @Test
    void incompleteDumpIsDeleted()
        throws IOException
    {
        final String dumpName = "incomplete-dump";

        try (ZipDumpWriter writer = ZipDumpWriter.create( tempDir, dumpName, null ))
        {
            writer.openVersionsMeta( RepositoryId.from( "test-repo" ) );
            writer.closeMeta();
        }

        assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip.partial" ) ) );
        assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip" ) ) );
    }

    @Test
    void failsIfDumpExists()
        throws IOException
    {
        final String dumpName = "existing-dump";
        final Path existing = Files.writeString( tempDir.resolve( dumpName + ".zip" ), "old" );

        assertThrows( RepoDumpException.class, () -> ZipDumpWriter.create( tempDir, dumpName, null ) );
        assertEquals( "old", Files.readString( existing ) );
        assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip.partial" ) ) );
    }

    @Test
    void failsIfPartialDumpExists()
        throws IOException
    {
        final String dumpName = "running-dump";
        final Path partial = Files.writeString( tempDir.resolve( dumpName + ".zip.partial" ), "running" );

        assertThrows( RepoDumpException.class, () -> ZipDumpWriter.create( tempDir, dumpName, null ) );
        assertEquals( "running", Files.readString( partial ) );
    }

    @Test
    void closeIsIdempotent()
        throws IOException
    {
        final String dumpName = "closeable-dump";
        final ZipDumpWriter writer = ZipDumpWriter.create( tempDir, dumpName, null );
        writer.writeDumpMetaData( dumpMeta() );
        writer.close();
        writer.close();

        assertTrue( Files.exists( tempDir.resolve( dumpName + ".zip" ) ) );
    }

    @Test
    void closeFailsIfDumpAppearedWhileWriting()
        throws IOException
    {
        final String dumpName = "racing-dump";
        final ZipDumpWriter writer = ZipDumpWriter.create( tempDir, dumpName, null );
        writer.writeDumpMetaData( dumpMeta() );
        final Path existing = Files.writeString( tempDir.resolve( dumpName + ".zip" ), "other" );

        assertThrows( FileAlreadyExistsException.class, writer::close );
        assertEquals( "other", Files.readString( existing ) );
        assertFalse( Files.exists( tempDir.resolve( dumpName + ".zip.partial" ) ) );
    }

    private static DumpMeta dumpMeta()
    {
        return DumpMeta.create().xpVersion( "7.16.0" ).modelVersion( DumpConstants.MODEL_VERSION ).timestamp( Instant.now() )
            .systemDumpResult( SystemDumpResult.create().build() )
            .build();
    }
}
