package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Test
    void testDeepNestedPaths()
        throws IOException
    {
        final String exportName = "deep-nested";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            // Create deep path: a/b/c/d/e/f/file.xml
            Path deepPath = baseDir;
            for ( String segment : List.of( "a", "b", "c", "d", "e", "f" ) )
            {
                deepPath = deepPath.resolve( segment );
            }
            writer.writeElement( deepPath.resolve( "file.xml" ), "deep content" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertTrue( Files.exists( fs.getPath( "/deep-nested/a/b/c/d/e/f/file.xml" ) ) );
            assertEquals( "deep content", Files.readString( fs.getPath( "/deep-nested/a/b/c/d/e/f/file.xml" ) ) );
        }
    }

    @Test
    void testMultipleFilesInSameDirectory()
        throws IOException
    {
        final String exportName = "multi-files";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "dir" ).resolve( "file1.txt" ), "content1" );
            writer.writeElement( baseDir.resolve( "dir" ).resolve( "file2.txt" ), "content2" );
            writer.writeElement( baseDir.resolve( "dir" ).resolve( "file3.txt" ), "content3" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertEquals( "content1", Files.readString( fs.getPath( "/multi-files/dir/file1.txt" ) ) );
            assertEquals( "content2", Files.readString( fs.getPath( "/multi-files/dir/file2.txt" ) ) );
            assertEquals( "content3", Files.readString( fs.getPath( "/multi-files/dir/file3.txt" ) ) );
        }
    }

    @Test
    void testPathWithSpecialCharacters()
        throws IOException
    {
        final String exportName = "special-chars";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            // Paths with spaces and unicode characters
            writer.writeElement( baseDir.resolve( "folder with spaces" ).resolve( "file.txt" ), "spaces" );
            writer.writeElement( baseDir.resolve( "unicode-файл" ).resolve( "данные.xml" ), "unicode content" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertEquals( "spaces", Files.readString( fs.getPath( "/special-chars/folder with spaces/file.txt" ) ) );
            assertEquals( "unicode content", Files.readString( fs.getPath( "/special-chars/unicode-файл/данные.xml" ) ) );
        }
    }

    @Test
    void testAbsolutePathHandling()
        throws IOException
    {
        final String exportName = "absolute-path";
        final Path baseDir = tempDir.resolve( exportName ).toAbsolutePath();

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            // Use absolute path explicitly
            writer.writeElement( baseDir.resolve( "subdir" ).resolve( "file.txt" ), "absolute" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            assertTrue( Files.exists( fs.getPath( "/absolute-path/subdir/file.txt" ) ) );
            assertEquals( "absolute", Files.readString( fs.getPath( "/absolute-path/subdir/file.txt" ) ) );
        }
    }

    @Test
    void testPathOutsideBaseDirectoryThrowsException()
        throws IOException
    {
        final String exportName = "path-traversal-test";

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            // Try to write outside base directory
            final Path outsidePath = tempDir.resolve( "outside" ).resolve( "file.txt" );

            assertThrows( ExportNodeException.class, () -> writer.writeElement( outsidePath, "malicious" ) );
        }
    }

    @Test
    void testPathTraversalAttackPrevented()
        throws IOException
    {
        final String exportName = "traversal-attack";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            // Try path traversal with ..
            final Path traversalPath = baseDir.resolve( ".." ).resolve( "outside" ).resolve( "file.txt" );

            assertThrows( ExportNodeException.class, () -> writer.writeElement( traversalPath, "attack" ) );
        }
    }

    @Test
    void testBinarySourceWithLargeContent()
        throws IOException
    {
        final String exportName = "large-binary";
        final Path baseDir = tempDir.resolve( exportName );

        // Create 1MB of data
        final byte[] largeData = new byte[1024 * 1024];
        for ( int i = 0; i < largeData.length; i++ )
        {
            largeData[i] = (byte) ( i % 256 );
        }

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeSource( baseDir.resolve( "large.bin" ), ByteSource.wrap( largeData ) );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        try (var fs = FileSystems.newFileSystem( URI.create( "jar:" + zipFile.toUri() ), Map.of() ))
        {
            final byte[] readData = Files.readAllBytes( fs.getPath( "/large-binary/large.bin" ) );
            assertEquals( largeData.length, readData.length );
            for ( int i = 0; i < largeData.length; i++ )
            {
                assertEquals( largeData[i], readData[i], "Byte mismatch at position " + i );
            }
        }
    }

    @Test
    void testZipEntryPathsUseForwardSlash()
        throws IOException
    {
        final String exportName = "forward-slash";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "a" ).resolve( "b" ).resolve( "c.txt" ), "content" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );

        // Read zip entries directly to verify they use forward slashes
        final List<String> entryNames = new ArrayList<>();
        try (var zipInputStream = new java.util.zip.ZipInputStream( Files.newInputStream( zipFile ) ))
        {
            java.util.zip.ZipEntry entry;
            while ( ( entry = zipInputStream.getNextEntry() ) != null )
            {
                entryNames.add( entry.getName() );
            }
        }

        // Verify all paths use forward slashes (not backslashes)
        for ( String entryName : entryNames )
        {
            assertTrue( !entryName.contains( "\\" ), "Entry should not contain backslash: " + entryName );
            assertTrue( entryName.startsWith( "forward-slash/" ), "Entry should start with export name: " + entryName );
        }

        assertTrue( entryNames.contains( "forward-slash/a/b/c.txt" ) );
    }

    @Test
    void testEmptyExportName()
    {
        assertThrows( IllegalArgumentException.class, () -> ZipExportWriter.create( tempDir, "" ) );
    }

    @Test
    void testExportNameWithPathSeparator()
    {
        assertThrows( IllegalArgumentException.class, () -> ZipExportWriter.create( tempDir, "path/separator" ) );
        assertThrows( IllegalArgumentException.class, () -> ZipExportWriter.create( tempDir, "path\\separator" ) );
    }
}
