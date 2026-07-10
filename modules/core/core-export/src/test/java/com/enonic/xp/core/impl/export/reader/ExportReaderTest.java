package com.enonic.xp.core.impl.export.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.writeString;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportReaderTest
{
    @TempDir
    Path tempDir;

    @Test
    void testGetChildrenFiltersSystemFolderForZipExport()
        throws IOException
    {
        final String exportName = "my-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "_" ).resolve( "node.xml" ), "<root/>" );
            writer.writeElement( baseDir.resolve( "my content" ).resolve( "_" ).resolve( "node.xml" ), "<child/>" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        final List<VirtualFile> children =
            assertDoesNotThrow( () -> new ExportReader().getChildren( root ).collect( Collectors.toList() ) );

        assertEquals( 1, children.size() );
        assertTrue( children.stream().anyMatch( child -> "my content".equals( child.getName() ) ) );
    }

    @Test
    void testGetChildrenFiltersSystemFolderForDirectoryExport()
        throws IOException
    {
        final Path exportDir = tempDir.resolve( "my-export" );
        createDirectories( exportDir.resolve( "_" ) );
        writeString( exportDir.resolve( "_" ).resolve( "node.xml" ), "<root/>" );
        createDirectories( exportDir.resolve( "mynode" ).resolve( "_" ) );
        writeString( exportDir.resolve( "mynode" ).resolve( "_" ).resolve( "node.xml" ), "<child/>" );

        final VirtualFile root = VirtualFiles.from( exportDir );

        final List<VirtualFile> children = new ExportReader().getChildren( root ).collect( Collectors.toList() );

        assertEquals( 1, children.size() );
        assertTrue( children.stream().anyMatch( child -> "mynode".equals( child.getName() ) ) );
    }
}
