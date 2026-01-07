package com.enonic.xp.core.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.export.NodeExporter;
import com.enonic.xp.core.impl.export.NodeImporter;
import com.enonic.xp.core.impl.export.reader.ZipVirtualFile;
import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.vfs.VirtualFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompressedExportImportIntegrationTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void export_and_import_single_node_compressed()
        throws IOException
    {
        // Create a node
        final Node myNode = createNode( NodePath.ROOT, "myNode" );

        // Export to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        final NodeExportResult exportResult = exportToZip( "single-node-export", NodePath.ROOT, exportDir );

        assertEquals( 2, exportResult.size() ); // root + myNode
        assertEquals( 0, exportResult.getExportErrors().size() );

        // Verify zip file exists
        final Path zipFile = exportDir.resolve( "single-node-export.zip" );
        assertTrue( Files.exists( zipFile ), "Zip file should exist" );
        assertTrue( Files.size( zipFile ) > 0, "Zip file should not be empty" );

        // Delete the original node (but not root)
        this.nodeService.delete( DeleteNodeParams.create().nodeId( myNode.id() ).build() );
        refresh();
        assertThat( getNode( myNode.id() ) ).isNull();

        // Import from zip
        final NodeImportResult importResult = importFromZip( zipFile, NodePath.ROOT );

        assertEquals( 0, importResult.getImportErrors().size() );
        // root is updated (already exists), myNode is added
        assertEquals( 1, importResult.getAddedNodes().getSize() ); // myNode
        assertEquals( 1, importResult.getUpdateNodes().getSize() ); // root

        // Verify node was recreated
        final Node importedNode = getNodeByPath( new NodePath( "/myNode" ) );
        assertNotNull( importedNode );
        assertEquals( myNode.name(), importedNode.name() );
    }


    @Test
    void export_and_import_deep_node_tree_compressed()
        throws IOException
    {
        // Create a deep node tree
        final Node level1 = createNode( NodePath.ROOT, "level1" );
        final Node level2_1 = createNode( level1.path(), "level2_1" );
        final Node level2_2 = createNode( level1.path(), "level2_2" );
        final Node level3_1 = createNode( level2_1.path(), "level3_1" );
        final Node level3_2 = createNode( level2_1.path(), "level3_2" );
        final Node level3_3 = createNode( level2_2.path(), "level3_3" );
        final Node level4_1 = createNode( level3_1.path(), "level4_1" );
        final Node level4_2 = createNode( level3_1.path(), "level4_2" );
        final Node level4_3 = createNode( level3_2.path(), "level4_3" );

        refresh();

        // Export to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        final NodeExportResult exportResult = exportToZip( "deep-tree-export", NodePath.ROOT, exportDir );

        // Should export root + 9 nodes
        assertEquals( 10, exportResult.size() );
        assertEquals( 0, exportResult.getExportErrors().size() );

        // Verify zip file
        final Path zipFile = exportDir.resolve( "deep-tree-export.zip" );
        assertTrue( Files.exists( zipFile ) );

        // Delete all nodes
        this.nodeService.delete( DeleteNodeParams.create().nodeId( level1.id() ).build() );
        refresh();

        // Import from zip
        final NodeImportResult importResult = importFromZip( zipFile, NodePath.ROOT );

        assertEquals( 0, importResult.getImportErrors().size() );
        // root is updated, 9 child nodes are added
        assertEquals( 9, importResult.getAddedNodes().getSize() );
        assertEquals( 1, importResult.getUpdateNodes().getSize() ); // root

        // Verify all nodes were recreated
        assertNotNull( getNodeByPath( new NodePath( "/level1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_2" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1/level3_1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1/level3_2" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_2/level3_3" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1/level3_1/level4_1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1/level3_1/level4_2" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/level1/level2_1/level3_2/level4_3" ) ) );
    }

    @Test
    void export_and_import_nodes_with_binaries_compressed()
        throws IOException
    {
        // Create nodes with binary attachments
        final BinaryReference binaryRef1 = BinaryReference.from( "image1.jpg" );
        final BinaryReference binaryRef2 = BinaryReference.from( "document.pdf" );

        final PropertyTree data1 = new PropertyTree();
        data1.addBinaryReference( "myImage", binaryRef1 );
        data1.addString( "title", "Node with image" );

        final Node nodeWithBinary1 = createNode( CreateNodeParams.create()
            .parent( NodePath.ROOT )
            .name( "node-with-binary-1" )
            .data( data1 )
            .attachBinary( binaryRef1, ByteSource.wrap( "fake image data".getBytes() ) )
            .build() );

        final PropertyTree data2 = new PropertyTree();
        data2.addBinaryReference( "myDoc", binaryRef2 );
        data2.addString( "title", "Node with document" );

        final Node nodeWithBinary2 = createNode( CreateNodeParams.create()
            .parent( NodePath.ROOT )
            .name( "node-with-binary-2" )
            .data( data2 )
            .attachBinary( binaryRef2, ByteSource.wrap( "fake pdf data".getBytes() ) )
            .build() );

        refresh();

        // Export to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        final NodeExportResult exportResult = exportToZip( "binary-export", NodePath.ROOT, exportDir );

        assertEquals( 3, exportResult.size() ); // root + 2 nodes
        assertEquals( 2, exportResult.getExportedBinaries().size() );
        assertEquals( 0, exportResult.getExportErrors().size() );

        // Delete nodes
        this.nodeService.delete( DeleteNodeParams.create().nodeId( nodeWithBinary1.id() ).build() );
        this.nodeService.delete( DeleteNodeParams.create().nodeId( nodeWithBinary2.id() ).build() );
        refresh();

        // Import from zip
        final Path zipFile = exportDir.resolve( "binary-export.zip" );
        final NodeImportResult importResult = importFromZip( zipFile, NodePath.ROOT );

        assertEquals( 0, importResult.getImportErrors().size() );
        // root is updated, 2 nodes with binaries are added
        assertEquals( 2, importResult.getAddedNodes().getSize() );
        assertEquals( 1, importResult.getUpdateNodes().getSize() ); // root

        // Verify nodes and binaries
        final Node imported1 = getNodeByPath( new NodePath( "/node-with-binary-1" ) );
        assertNotNull( imported1 );
        assertEquals( 1, imported1.getAttachedBinaries().getSize() );

        final Node imported2 = getNodeByPath( new NodePath( "/node-with-binary-2" ) );
        assertNotNull( imported2 );
        assertEquals( 1, imported2.getAttachedBinaries().getSize() );
    }

    @Test
    void export_and_import_with_child_order_compressed()
        throws IOException
    {
        // Create parent with manual child order
        final Node parent = createNode( CreateNodeParams.create()
            .parent( NodePath.ROOT )
            .name( "ordered-parent" )
            .childOrder( ChildOrder.manualOrder() )
            .build() );

        // Create children
        createNode( parent.path(), "child1" );
        createNode( parent.path(), "child2" );
        createNode( parent.path(), "child3" );
        createNode( parent.path(), "child4" );

        refresh();

        // Export to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        final NodeExportResult exportResult = exportToZip( "ordered-export", NodePath.ROOT, exportDir );

        assertEquals( 6, exportResult.size() ); // root + parent + 4 children
        assertEquals( 0, exportResult.getExportErrors().size() );

        // Delete nodes
        this.nodeService.delete( DeleteNodeParams.create().nodeId( parent.id() ).build() );
        refresh();

        // Import from zip
        final Path zipFile = exportDir.resolve( "ordered-export.zip" );
        final NodeImportResult importResult = importFromZip( zipFile, NodePath.ROOT );

        assertEquals( 0, importResult.getImportErrors().size() );

        // Verify parent and children
        final Node importedParent = getNodeByPath( new NodePath( "/ordered-parent" ) );
        assertNotNull( importedParent );
        assertEquals( ChildOrder.manualOrder(), importedParent.getChildOrder() );

        assertNotNull( getNodeByPath( new NodePath( "/ordered-parent/child1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/ordered-parent/child2" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/ordered-parent/child3" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/ordered-parent/child4" ) ) );
    }

    @Test
    void export_and_import_subtree_compressed()
        throws IOException
    {
        // Create tree structure
        final Node root = createNode( NodePath.ROOT, "myroot" );
        final Node child1 = createNode( root.path(), "child1" );
        final Node child2 = createNode( root.path(), "child2" );
        final Node subchild1 = createNode( child1.path(), "subchild1" );
        final Node subchild2 = createNode( child1.path(), "subchild2" );

        refresh();

        // Export only subtree starting from child1
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        final NodeExportResult exportResult = exportToZip( "subtree-export", child1.path(), exportDir );

        // Should export child1 + 2 subchildren
        assertEquals( 3, exportResult.size() );
        assertEquals( 0, exportResult.getExportErrors().size() );

        // Delete the subtree
        this.nodeService.delete( DeleteNodeParams.create().nodeId( child1.id() ).build() );
        refresh();
        assertThat( getNode( child1.id() ) ).isNull();

        // Import subtree back under root
        final Path zipFile = exportDir.resolve( "subtree-export.zip" );
        final NodeImportResult importResult = importFromZip( zipFile, root.path() );

        assertEquals( 0, importResult.getImportErrors().size() );
        assertEquals( 3, importResult.getAddedNodes().getSize() );

        // Verify nodes
        assertNotNull( getNodeByPath( new NodePath( "/myroot/child1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/myroot/child1/subchild1" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/myroot/child1/subchild2" ) ) );
    }

    @Test
    void export_and_import_with_versions_compressed()
        throws IOException
    {
        // Create a node
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Initial Title" );

        final Node node = createNode( CreateNodeParams.create()
            .parent( NodePath.ROOT )
            .name( "versioned-node" )
            .data( data )
            .build() );

        // Update the node to create a version
        final Node updatedNode = updateNode( UpdateNodeParams.create()
            .id( node.id() )
            .editor( ( n ) -> {
                n.data.setString( "title", "Updated Title" );
            } )
            .build() );

        refresh();

        // Export with versions to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        try ( final ZipExportWriter writer = ZipExportWriter.create( exportDir, "version-export" ) )
        {
            final NodeExportResult exportResult = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( writer )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( exportDir.resolve( "version-export" ) )
                .exportVersions( true )
                .build()
                .execute();

            assertEquals( 2, exportResult.size() ); // root + node
            assertEquals( 0, exportResult.getExportErrors().size() );
        }

        // Delete node
        this.nodeService.delete( DeleteNodeParams.create().nodeId( updatedNode.id() ).build() );
        refresh();

        // Import from zip
        final Path zipFile = exportDir.resolve( "version-export.zip" );
        final NodeImportResult importResult = importFromZip( zipFile, NodePath.ROOT );

        assertEquals( 0, importResult.getImportErrors().size() );

        // Verify node was imported
        final Node importedNode = getNodeByPath( new NodePath( "/versioned-node" ) );
        assertNotNull( importedNode );
        assertEquals( "Updated Title", importedNode.data().getString( "title" ) );
    }

    @Test
    void verify_zip_file_structure()
        throws IOException
    {
        // Create a simple tree
        final Node parent = createNode( NodePath.ROOT, "parent" );
        createNode( parent.path(), "child" );

        refresh();

        // Export to zip
        final Path exportDir = temporaryFolder.resolve( "exports" );
        Files.createDirectories( exportDir );

        NodeExportResult nodeExportResult = exportToZip( "structure-test", parent.path(), exportDir );

        // Verify we can read the zip structure
        final Path zipFile = exportDir.resolve( "structure-test.zip" );
        final VirtualFile virtualFile = ZipVirtualFile.from( zipFile );

        assertNotNull( virtualFile );
        assertTrue( virtualFile.exists() );
        assertTrue( virtualFile.isFolder() );

        // Verify children exist
        final var children = virtualFile.getChildren();
        assertThat( children ).isNotEmpty();
    }

    // Helper methods

    private NodeExportResult exportToZip( final String exportName, final NodePath sourcePath, final Path exportDir )
        throws IOException
    {
        try ( final ZipExportWriter writer = ZipExportWriter.create( exportDir, exportName ) )
        {
            return NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( writer )
                .sourceNodePath( sourcePath )
                .targetDirectory( exportDir.resolve( exportName ) ).xpVersion( "1.1.0" )
                .build()
                .execute();
        }
    }

    private NodeImportResult importFromZip( final Path zipFile, final NodePath targetPath )
        throws IOException
    {
        final VirtualFile source = ZipVirtualFile.from( zipFile );

        return NodeImporter.create()
            .nodeService( this.nodeService )
            .sourceDirectory( source )
            .targetNodePath( targetPath )
            .build()
            .execute();
    }
}
