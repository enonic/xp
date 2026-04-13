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

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( "_" ).resolve( "node.xml" ), "<root/>" );
            writer.writeElement( baseDir.resolve( "mynode" ).resolve( "_" ).resolve( "node.xml" ), "<mynode/>" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        assertNotNull( root );
        assertEquals( exportName, root.getName() );
        assertTrue( root.exists() );
        assertTrue( root.isFolder() );

        var children = root.getChildren();
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

        // Test resolve works correctly
        final VirtualFile resolvedUnderscore = root.resolve( VirtualFilePaths.from( "_", "/" ) );
        assertNotNull( resolvedUnderscore );
        assertTrue( resolvedUnderscore.exists() );
        assertTrue( resolvedUnderscore.isFolder() );

        // Test isNodeFolder equivalent - resolve with full path
        final VirtualFile resolvedViaJoin = root.resolve( root.getPath().join( "_" ) );
        assertTrue( resolvedViaJoin.exists(), "resolve via join should find _ folder" );

        // Test that mynode/_ also works
        final VirtualFile mynode = root.resolve( VirtualFilePaths.from( "mynode", "/" ) );
        assertTrue( mynode.exists(), "mynode should exist" );
        assertTrue( mynode.isFolder(), "mynode should be a folder" );

        final VirtualFile mynodeUnderscore = mynode.resolve( VirtualFilePaths.from( "_", "/" ) );
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

        final VirtualFile a = root.resolve( VirtualFilePaths.from( "a", "/" ) );
        assertTrue( a.exists() );
        assertTrue( a.isFolder() );

        final VirtualFile b = a.resolve( VirtualFilePaths.from( "b", "/" ) );
        assertTrue( b.exists() );
        assertTrue( b.isFolder() );

        final VirtualFile c = b.resolve( VirtualFilePaths.from( "c.txt", "/" ) );
        assertTrue( c.exists() );
        assertTrue( c.isFile() );

        assertEquals( "content", c.getCharSource().read() );
    }

    @Test
    void testNodeExportStructure()
        throws IOException
    {
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

        // Test isNodeFolder logic: folder.resolve(folder.getPath().join("_")).exists()
        VirtualFilePath joinedPath = root.getPath().join( "_" );
        VirtualFile resolvedUnderscore = root.resolve( joinedPath );
        assertTrue( resolvedUnderscore.exists(), "isNodeFolder check should pass for root" );

        var children = root.getChildren();
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

        // Test isNodeFolder for myNode
        VirtualFilePath myNodeJoinedPath = myNodeFolder.getPath().join( "_" );
        VirtualFile myNodeUnderscore = myNodeFolder.resolve( myNodeJoinedPath );
        assertTrue( myNodeUnderscore.exists(), "isNodeFolder check should pass for myNode" );

        // Test processNodeSource logic
        VirtualFilePath nodeXmlPath = myNodeFolder.getPath().join( "_", "node.xml" );
        VirtualFile nodeXml = myNodeFolder.resolve( nodeXmlPath );
        assertTrue( nodeXml.exists(), "node.xml should exist" );
        assertTrue( nodeXml.isFile(), "node.xml should be a file" );
    }

    @Test
    void testIgnoresMacOSXSystemFolder()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "with-macosx.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            addZipEntry( zos, "__MACOSX/._file.txt", "macos metadata" );
            addZipEntry( zos, "my-export/_/node.xml", "<node/>" );
            addZipEntry( zos, "my-export/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );

        assertEquals( "my-export", root.getName() );
        assertTrue( root.exists() );

        var children = root.getChildren();
        assertEquals( 2, children.size() );
    }

    @Test
    void testSelectsFolderWithExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "multiple-folders.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            addZipEntry( zos, "other-folder/file.txt", "other" );
            addZipEntry( zos, "correct-export/_/node.xml", "<node/>" );
            addZipEntry( zos, "correct-export/export.properties", "xp.version=1.0" );
        }

        final VirtualFile root = ZipVirtualFile.from( zipFile );
        assertEquals( "correct-export", root.getName() );
    }

    @Test
    void testThrowsExceptionForMultipleFoldersWithExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "ambiguous.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            addZipEntry( zos, "folder1/export.properties", "xp.version=1.0" );
            addZipEntry( zos, "folder2/export.properties", "xp.version=1.0" );
        }

        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testThrowsExceptionForNoExportProperties()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "no-export-properties.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            addZipEntry( zos, "folder1/file.txt", "content1" );
        }

        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testThrowsExceptionForEmptyArchive()
        throws IOException
    {
        final Path zipFile = tempDir.resolve( "empty.zip" );

        try (OutputStream fos = Files.newOutputStream( zipFile ); ZipOutputStream zos = new ZipOutputStream( fos ))
        {
            // Empty zip
        }

        assertThrows( IllegalArgumentException.class, () -> ZipVirtualFile.from( zipFile ) );
    }

    @Test
    void testCorruptedZipFile()
        throws IOException
    {
        final Path corruptedZip = tempDir.resolve( "corrupted.zip" );
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

        final VirtualFile nonExistent = root.resolve( VirtualFilePaths.from( "does-not-exist.txt", "/" ) );
        assertThrows( Exception.class, () -> nonExistent.getCharSource().read() );
    }

    @Test
    void testDeepPath()
        throws IOException
    {
        final String exportName = "deep-path";
        final Path baseDir = tempDir.resolve( exportName );

        StringBuilder longPath = new StringBuilder();
        for ( int i = 0; i < 20; i++ )
        {
            longPath.append( "folder-" ).append( i ).append( "/" );
        }
        longPath.append( "file.txt" );

        try (ZipExportWriter writer = ZipExportWriter.create( tempDir, exportName ))
        {
            writer.writeElement( baseDir.resolve( longPath.toString() ), "deep content" );
            writer.writeElement( baseDir.resolve( "export.properties" ), "xp.version=1.0" );
        }

        final Path zipFile = tempDir.resolve( exportName + ".zip" );
        final VirtualFile root = ZipVirtualFile.from( zipFile );

        VirtualFile current = root;
        for ( int i = 0; i < 20; i++ )
        {
            current = current.resolve( VirtualFilePaths.from( "folder-" + i, "/" ) );
            assertTrue( current.exists() );
        }

        final VirtualFile file = current.resolve( VirtualFilePaths.from( "file.txt", "/" ) );
        assertTrue( file.exists() );
        assertEquals( "deep content", file.getCharSource().read() );
    }

    private void addZipEntry( final ZipOutputStream zos, final String path, final String content )
        throws IOException
    {
        zos.putNextEntry( new ZipEntry( path ) );
        zos.write( content.getBytes( StandardCharsets.UTF_8 ) );
        zos.closeEntry();
    }
}
