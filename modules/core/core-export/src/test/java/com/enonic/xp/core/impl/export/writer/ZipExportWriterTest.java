package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipExportWriterTest
{
    @TempDir
    Path tempDir;

    @Test
    void testZipExportWriterCreatesZipFile()
        throws IOException
    {
        final String exportName = "test-export";

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( Path.of( "test.txt" ), "Hello, World!" );
            writer.writeSource( Path.of( "data.bin" ), ByteSource.wrap( "Binary data".getBytes( StandardCharsets.UTF_8 ) ) );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should be created" );
        assertTrue( Files.size( zipFile ) > 0, "Zip file should not be empty" );
    }

    @Test
    void testZipExportWriterCanBeClosedMultipleTimes()
        throws IOException
    {
        final String exportName = "test-export-multiple-close";

        final ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName );
        writer.writeElement( Path.of( "test.txt" ), "Hello, World!" );
        writer.close();
        writer.close(); // Should not throw exception

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should be created" );
    }
}
