package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "test.txt" ), "Hello, World!" );
            writer.writeSource( baseDir.resolve( "data.bin" ), ByteSource.wrap( "Binary data".getBytes( StandardCharsets.UTF_8 ) ) );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should be created" );
        assertTrue( Files.size( zipFile ) > 0, "Zip file should not be empty" );

        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertTrue( Files.exists( fs.getPath( "/test-export/test.txt" ) ), "Entry should be inside export folder" );
            assertTrue( Files.exists( fs.getPath( "/test-export/data.bin" ) ), "Binary entry should be inside export folder" );
            final String content = Files.readString( fs.getPath( "/test-export/test.txt" ), StandardCharsets.UTF_8 );
            assertTrue( content.equals( "Hello, World!" ), "Content should be preserved" );
        }
    }

    @Test
    void testZipExportWriterNestedPathIsRelativeToExportRoot()
        throws IOException
    {
        final String exportName = "test-export-nested";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "a" ).resolve( "b" ).resolve( "node.xml" ), "data" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertTrue( Files.exists( fs.getPath( "/test-export-nested/a/b/node.xml" ) ), "Nested entry should be inside export folder" );
        }
    }

    @Test
    void testZipExportWriterCanBeClosedMultipleTimes()
        throws IOException
    {
        final String exportName = "test-export-multiple-close";
        final Path baseDir = tempDir.resolve( exportName );

        final ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName );
        writer.writeElement( baseDir.resolve( "test.txt" ), "Hello, World!" );
        writer.close();
        writer.close(); // Should not throw exception

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should be created" );
    }
}
