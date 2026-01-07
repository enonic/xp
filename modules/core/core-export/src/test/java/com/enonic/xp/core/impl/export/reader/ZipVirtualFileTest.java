package com.enonic.xp.core.impl.export.reader;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}

