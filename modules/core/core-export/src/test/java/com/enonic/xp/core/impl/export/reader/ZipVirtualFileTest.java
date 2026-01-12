package com.enonic.xp.core.impl.export.reader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipVirtualFileTest
{
    @TempDir
    Path tempDir;

    @Test
    void testZipVirtualFileStructure()
        throws IOException
    {
        final String exportName = "test-export";
        final Path baseDir = tempDir.resolve( exportName );

        // Create zip with structure like NodeExporter would create:
        // test-export/_/node.xml
        // test-export/mynode/_/node.xml
        // test-export/export.properties
        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "_" ).resolve( "node.xml" ), "<root/>" );
            writer.writeElement( baseDir.resolve( "mynode" ).resolve( "_" ).resolve( "node.xml" ), "<mynode/>" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        // Read via ZipVirtualFile
        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Root should be the export folder
        assertNotNull( root );
        assertEquals( exportName, root.getName() );
        assertTrue( root.exists() );
        assertTrue( root.isFolder() );

        // Check children
        var children = root.getChildren();
        System.out.println( "Root children: " + children.size() );
        for ( var child : children )
        {
            System.out.println(
                "  - " + child.getName() + " path=" + child.getPath().getPath() + " (folder=" + child.isFolder() + ", file=" +
                    child.isFile() + ", exists=" + child.exists() + ")" );
            if ( child.isFolder() )
            {
                for ( var subchild : child.getChildren() )
                {
                    System.out.println(
                        "      - " + subchild.getName() + " path=" + subchild.getPath().getPath() + " (folder=" + subchild.isFolder() +
                            ", file=" + subchild.isFile() + ", exists=" + subchild.exists() + ")" );
                }
            }
        }

        // Should have 3 children: _, mynode, export.properties
        assertEquals( 3, children.size() );

        // Check that _ folder exists as a child
        VirtualFile underscoreFolder = null;
        for ( var child : children )
        {
            if ( "_".equals( child.getName() ) )
            {
                underscoreFolder = child;
                break;
            }
        }
        assertNotNull( underscoreFolder, "_ folder should exist" );
        assertTrue( underscoreFolder.isFolder() );

        // Check resolve works correctly
        final VirtualFile resolvedUnderscore = root.resolve( VirtualFilePaths.from( "_", "/" ) );
        assertNotNull( resolvedUnderscore );
        assertTrue( resolvedUnderscore.exists() );
        assertTrue( resolvedUnderscore.isFolder() );

        // Test isNodeFolder equivalent - resolve with full path
        final VirtualFile resolvedViaJoin = root.resolve( root.getPath().join( "_" ) );
        System.out.println( "root.getPath() = " + root.getPath().getPath() );
        System.out.println( "root.getPath().join('_') = " + root.getPath().join( "_" ).getPath() );
        System.out.println( "resolvedViaJoin path = " + resolvedViaJoin.getPath().getPath() );
        System.out.println( "resolvedViaJoin.exists() = " + resolvedViaJoin.exists() );
        assertTrue( resolvedViaJoin.exists(), "resolve via join should find _ folder" );

        // Test that mynode/_ also works
        final VirtualFile mynode = root.resolve( VirtualFilePaths.from( "mynode", "/" ) );
        assertTrue( mynode.exists(), "mynode should exist" );
        assertTrue( mynode.isFolder(), "mynode should be a folder" );

        final VirtualFile mynodeUnderscore = mynode.resolve( VirtualFilePaths.from( "_", "/" ) );
        System.out.println( "mynode/_: path=" + mynodeUnderscore.getPath().getPath() + " exists=" + mynodeUnderscore.exists() );
        assertTrue( mynodeUnderscore.exists(), "mynode/_ should exist" );
    }

    @Test
    void testResolveRelativePath()
        throws IOException
    {
        final String exportName = "my-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "a" ).resolve( "b" ).resolve( "c.txt" ), "content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Resolve relative path
        final VirtualFile a = root.resolve( VirtualFilePaths.from( "a", "/" ) );
        assertTrue( a.exists() );
        assertTrue( a.isFolder() );

        final VirtualFile b = a.resolve( VirtualFilePaths.from( "b", "/" ) );
        assertTrue( b.exists() );
        assertTrue( b.isFolder() );

        final VirtualFile c = b.resolve( VirtualFilePaths.from( "c.txt", "/" ) );
        assertTrue( c.exists() );
        assertTrue( c.isFile() );

        // Read content
        assertEquals( "content", c.getCharSource().read() );
    }

    @Test
    void testNodeExportStructure()
        throws IOException
    {
        // Simulate NodeExporter structure:
        // exportName/_/node.xml  (root node)
        // exportName/myNode/_/node.xml  (child node)
        // exportName/export.properties
        final String exportName = "node-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "_" ).resolve( "node.xml" ), "<root/>" );
            writer.writeElement( baseDir.resolve( "myNode" ).resolve( "_" ).resolve( "node.xml" ), "<myNode/>" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        System.out.println( "=== Testing NodeExport structure ===" );
        System.out.println( "root.getName() = " + root.getName() );
        System.out.println( "root.getPath().getPath() = " + root.getPath().getPath() );

        // Test isNodeFolder logic: folder.resolve(folder.getPath().join("_")).exists()
        VirtualFilePath joinedPath = root.getPath().join( "_" );
        System.out.println( "root.getPath().join('_') = " + joinedPath.getPath() );

        VirtualFile resolvedUnderscore = root.resolve( joinedPath );
        System.out.println( "root.resolve(joinedPath).getPath() = " + resolvedUnderscore.getPath().getPath() );
        System.out.println( "root.resolve(joinedPath).exists() = " + resolvedUnderscore.exists() );

        assertTrue( resolvedUnderscore.exists(), "isNodeFolder check should pass for root" );

        // Check children (should be: _, myNode, export.properties)
        var children = root.getChildren();
        System.out.println( "root.getChildren().size() = " + children.size() );
        for ( var child : children )
        {
            System.out.println(
                "  child: " + child.getName() + " path=" + child.getPath().getPath() + " isFolder=" + child.isFolder() + " exists=" +
                    child.exists() );
        }
        assertEquals( 3, children.size() );

        // Find myNode child
        VirtualFile myNodeFolder = null;
        for ( var child : children )
        {
            if ( "myNode".equals( child.getName() ) )
            {
                myNodeFolder = child;
                break;
            }
        }
        assertNotNull( myNodeFolder, "myNode folder should exist" );
        assertTrue( myNodeFolder.isFolder() );

        // Test isNodeFolder for myNode: myNodeFolder.resolve(myNodeFolder.getPath().join("_")).exists()
        VirtualFilePath myNodeJoinedPath = myNodeFolder.getPath().join( "_" );
        System.out.println( "myNodeFolder.getPath().join('_') = " + myNodeJoinedPath.getPath() );

        VirtualFile myNodeUnderscore = myNodeFolder.resolve( myNodeJoinedPath );
        System.out.println( "myNodeFolder.resolve(joinedPath).getPath() = " + myNodeUnderscore.getPath().getPath() );
        System.out.println( "myNodeFolder.resolve(joinedPath).exists() = " + myNodeUnderscore.exists() );

        assertTrue( myNodeUnderscore.exists(), "isNodeFolder check should pass for myNode" );

        // Test processNodeSource logic: nodeFolder.resolve(nodeFolder.getPath().join("_", "node.xml")).exists()
        VirtualFilePath nodeXmlPath = myNodeFolder.getPath().join( "_", "node.xml" );
        System.out.println( "myNodeFolder.getPath().join('_', 'node.xml') = " + nodeXmlPath.getPath() );

        VirtualFile nodeXml = myNodeFolder.resolve( nodeXmlPath );
        System.out.println( "myNodeFolder.resolve(nodeXmlPath).getPath() = " + nodeXml.getPath().getPath() );
        System.out.println( "myNodeFolder.resolve(nodeXmlPath).exists() = " + nodeXml.exists() );

        assertTrue( nodeXml.exists(), "node.xml should exist" );
        assertTrue( nodeXml.isFile(), "node.xml should be a file" );
    }

    @Test
    void testIgnoresMacOSXSystemFolder()
        throws IOException
    {
        // Create zip with __MACOSX folder (as macOS does when zipping)
        final Path zipFile = tempDir.resolve( "with-macosx.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Add __MACOSX folder (should be ignored because no export.properties)
            addZipEntry( zos, "__MACOSX/._file.txt", "macos metadata" );

            // Add actual export folder with export.properties
            addZipEntry( zos, "my-export/_/node.xml", "<node/>" );
            addZipEntry( zos, "my-export/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Should use my-export as base (has export.properties), not __MACOSX
        assertEquals( "my-export", root.getName() );
        assertTrue( root.exists() );

        // Should have 2 children: _ and export.properties (not __MACOSX)
        var children = root.getChildren();
        assertEquals( 2, children.size() );
    }

    @Test
    void testIgnoresHiddenFolders()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "with-hidden.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Add hidden folder (should be ignored because no export.properties)
            addZipEntry( zos, ".git/config", "git config" );

            // Add actual export folder with export.properties
            addZipEntry( zos, "export-data/_/node.xml", "<node/>" );
            addZipEntry( zos, "export-data/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Should use export-data as base (has export.properties), not .git
        assertEquals( "export-data", root.getName() );
    }

    @Test
    void testSelectsFolderWithExportProperties()
        throws IOException
    {
        // Create zip with multiple root folders, but only one has export.properties
        final Path zipFile = tempDir.resolve( "multiple-folders.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Add folder without export.properties
            addZipEntry( zos, "other-folder/file.txt", "other" );

            // Add folder with export.properties
            addZipEntry( zos, "correct-export/_/node.xml", "<node/>" );
            addZipEntry( zos, "correct-export/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Should use correct-export (has export.properties)
        assertEquals( "correct-export", root.getName() );
    }

    @Test
    void testThrowsExceptionForMultipleFoldersWithExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "ambiguous.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Add multiple folders with export.properties
            addZipEntry( zos, "folder1/export.properties", "xp.version=1.0" );
            addZipEntry( zos, "folder2/export.properties", "xp.version=1.0" );
        }

        // Should throw exception because multiple folders have export.properties
        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testThrowsExceptionForNoExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "no-export-properties.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Add folders without export.properties
            addZipEntry( zos, "folder1/file.txt", "content1" );
            addZipEntry( zos, "folder2/file.txt", "content2" );
        }

        // Should throw exception because no folder has export.properties
        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testThrowsExceptionForEmptyArchive()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "empty.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Empty zip - no entries
        }

        // Should throw exception because no folder with export.properties found
        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testSingleFolderWithExportProperties()
        throws IOException
    {
        // Archive name doesn't match folder name, but folder has export.properties
        final Path zipFile = tempDir.resolve( "archive.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            addZipEntry( zos, "different-name/_/node.xml", "<node/>" );
            addZipEntry( zos, "different-name/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Should use the folder with export.properties
        assertEquals( "different-name", root.getName() );
    }

    private void addZipEntry( final ZipOutputStream zos, final String path, final String content )
        throws IOException
    {
        zos.putNextEntry( new ZipEntry( path ) );
        zos.write( content.getBytes( StandardCharsets.UTF_8 ) );
        zos.closeEntry();
    }

    @Test
    void testNonExistentZipFile()
    {
        final Path nonExistentZip = tempDir.resolve( "does-not-exist.zip" );

        assertThrows( Exception.class, () -> ZipVirtualFile.from( nonExistentZip ) );
    }

    @Test
    void testCorruptedZipFile()
        throws IOException
    {
        final Path corruptedZip = tempDir.resolve( "corrupted.zip" );

        // Create a file with .zip extension but not a valid zip
        Files.writeString( corruptedZip, "This is not a zip file" );

        assertThrows( Exception.class, () -> ZipVirtualFile.from( corruptedZip ) );
    }

    @Test
    void testReadNonExistentFile()
        throws IOException
    {
        final String exportName = "test-export";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "file.txt" ), "content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Try to read a non-existent file
        final VirtualFile nonExistent = root.resolve( VirtualFilePaths.from( "does-not-exist.txt", "/" ) );

        assertThrows( Exception.class, () -> nonExistent.getCharSource().read() );
    }

    @Test
    void testResolveAbsolutePathOutsideBase()
        throws IOException
    {
        final String exportName = "path-test";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "file.txt" ), "content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // This should work - just resolve relative to root
        final VirtualFile file = root.resolve( VirtualFilePaths.from( "file.txt", "/" ) );
        assertTrue( file.exists() );
    }

    @Test
    void testGetURLForNonExistentFile()
        throws IOException
    {
        final String exportName = "url-test";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "file.txt" ), "content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        final VirtualFile nonExistent = root.resolve( VirtualFilePaths.from( "does-not-exist.txt", "/" ) );

        // Getting URL should not throw even if file doesn't exist
        assertNotNull( nonExistent.getUrl() );
    }

    @Test
    void testZipWithOnlySystemFolders()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "only-system.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Only system folders
            addZipEntry( zos, "__MACOSX/file.txt", "macos" );
            addZipEntry( zos, ".git/config", "git" );
        }

        // Should throw because no valid folder with export.properties
        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testZipWithDeepExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "deep-props.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // export.properties at wrong depth (should be at depth 1)
            addZipEntry( zos, "folder/subfolder/export.properties", "xp.version=1.0" );
            addZipEntry( zos, "folder/subfolder/data.txt", "data" );
        }

        // Should throw because export.properties not at depth 1
        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testNullZipPath()
    {
        assertThrows( NullPointerException.class, () -> ZipVirtualFile.from( null ) );
    }

    @Test
    void testGetByteSourceForDirectory()
        throws IOException
    {
        final String exportName = "dir-test";
        final Path baseDir = tempDir.resolve( exportName );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "dir" ).resolve( "file.txt" ), "content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        final VirtualFile dir = root.resolve( VirtualFilePaths.from( "dir", "/" ) );
        assertTrue( dir.isFolder() );

        // Trying to get ByteSource for a directory should throw or return null
        assertThrows( Exception.class, () -> dir.getByteSource().read() );
    }

    @Test
    void testVeryLongPath()
        throws IOException
    {
        final String exportName = "long-path";
        final Path baseDir = tempDir.resolve( exportName );

        // Create a very deep path
        StringBuilder longPath = new StringBuilder();
        for ( int i = 0; i < 50; i++ )
        {
            longPath.append( "very-long-folder-name-" ).append( i ).append( "/" );
        }
        longPath.append( "file.txt" );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( longPath.toString() ), "deep content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        // Navigate through the deep structure
        VirtualFile current = root;
        for ( int i = 0; i < 50; i++ )
        {
            current = current.resolve( VirtualFilePaths.from( "very-long-folder-name-" + i, "/" ) );
            assertTrue( current.exists() );
        }

        final VirtualFile file = current.resolve( VirtualFilePaths.from( "file.txt", "/" ) );
        assertTrue( file.exists() );
        assertEquals( "deep content", file.getCharSource().read() );
    }
}
