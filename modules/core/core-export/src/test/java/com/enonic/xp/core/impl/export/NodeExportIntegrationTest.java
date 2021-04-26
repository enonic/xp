package com.enonic.xp.core.impl.export;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.FileExportWriter;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.util.BinaryReference;

import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.BINARY_FOLDER;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.NODE_XML_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.SYSTEM_FOLDER_NAME;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.VERSION_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeExportIntegrationTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void single_node()
        throws Exception
    {
        final Node myNode = createNode( NodePath.ROOT, "myNode" );

        final NodeExportResult result = doExportRoot( false );

        assertEquals( 2, result.size() );
        assertExported( myNode );
    }

    @Test
    public void single_node_with_binary()
        throws Exception
    {
        final BinaryReference binaryRef = BinaryReference.from( "myFile" );
        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinary", binaryRef );

        final Node myNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is a binary file".getBytes() ) ).
            build() );

        final NodeExportResult result = doExportRoot( false );

        assertEquals( 2, result.size() );
        assertEquals( 1, result.getExportedBinaries().size() );
        assertExported( myNode );
        assertBinaryExported( myNode, binaryRef );
    }

    @Test
    public void single_node_multiple_versions()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.addInstant( "myInstant", Instant.now() ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        assertEquals( 2, result.size() );

        printPaths();

        assertExported( updatedNode );
        assertVersionExported( updatedNode, node );
    }

    @Test
    public void single_node_changed_name()
        throws Exception
    {
        final Node originalNode = createNode( NodePath.ROOT, "initial-name" );

        final Node renamedNode = this.nodeService.rename( RenameNodeParams.create().
            nodeId( originalNode.id() ).
            nodeName( NodeName.from( "new-node-name" ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        assertEquals( 2, result.size() );

        printPaths();
        assertExported( renamedNode );
        assertVersionExported( renamedNode, originalNode );
    }

    @Test
    public void single_node_with_binary_changed()
        throws Exception
    {
        final BinaryReference binaryRef = BinaryReference.from( "myFile" );
        final BinaryReference binaryRefUpdated = BinaryReference.from( "myOtherFile" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinary", binaryRef );

        final Node myNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is a binary file".getBytes() ) ).
            build() );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( myNode.id() ).
            editor( ( n ) -> n.data.setBinaryReference( "myBinary", binaryRefUpdated ) ).
            attachBinary( binaryRefUpdated, ByteSource.wrap( "this is another binary file".getBytes() ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        printPaths();

        assertEquals( 2, result.size() );
        assertExported( myNode );
        assertBinaryExported( updatedNode, binaryRefUpdated );
        assertVersionBinaryExported( updatedNode, myNode, binaryRef );
    }

    @Test
    public void one_node_file()
        throws Exception
    {
        createNode( "mynode", NodePath.ROOT );

        nodeService.refresh( RefreshMode.ALL );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 2, result.size() );

        assertFileExists( "myExport/_/node.xml" );
        assertFileExists( "myExport/mynode/_/node.xml" );
    }

    @Test
    public void children_nodes()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        createNode( "child1_1", child1.path() );
        final Node child1_2 = createNode( "child1_2", child1.path() );
        createNode( "child1_2_1", child1_2.path() );
        createNode( "child1_2_2", child1_2.path() );
        final Node child2 = createNode( "child2", root.path() );
        createNode( "child2_1", child2.path() );

        final NodeExportListener nodeExportListener = Mockito.mock( NodeExportListener.class );

        nodeService.refresh( RefreshMode.ALL );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            nodeExportListener( nodeExportListener ).
            build().
            execute();

        assertEquals( 9, result.size() );

        assertFileExists( "myExport/_/node.xml" );
        assertFileExists( "myExport/mynode/_/node.xml" );
        assertFileExists( "myExport/mynode/child1/_/node.xml" );
        assertFileExists( "myExport/mynode/child1/child1_1/_/node.xml" );
        assertFileExists( "myExport/mynode/child1/child1_2/_/node.xml" );
        assertFileExists( "myExport/mynode/child1/child1_2/child1_2_1/_/node.xml" );
        assertFileExists( "myExport/mynode/child1/child1_2/child1_2_2/_/node.xml" );
        assertFileExists( "myExport/mynode/child2/_/node.xml" );
        assertFileExists( "myExport/mynode/child2/child2_1/_/node.xml" );

        Mockito.verify( nodeExportListener ).
            nodeResolved( 9L );
        Mockito.verify( nodeExportListener, Mockito.times( 9 ) ).
            nodeExported( 1L );
    }

    @Test
    public void writerOrderList()
    {
        final Node root = Node.create().
            name( NodeName.from( "root" ) ).
            parentPath( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            build();

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        createNode( "child3", root.path() );
        createNode( "child4", root.path() );
        createNode( "child5", root.path() );
        createNode( "child6", root.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 8, result.size() );

        assertFileExists( "myExport/root/_/node.xml" );
        assertFileExists( "myExport/root/_/manualChildOrder.txt" );
    }


    @Test
    public void export_from_child_of_child()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        createNode( "child1_1_1", child1_1.path() );
        createNode( "child1_1_2", child1_1.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.create( "/mynode/child1/child1_1" ).build() ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 3, result.getExportedNodes().getSize() );

        assertFileExists( "myExport/child1_1/_/node.xml" );
        assertFileExists( "myExport/child1_1/child1_1_1/_/node.xml" );
        assertFileExists( "myExport/child1_1/child1_1_2/_/node.xml" );
    }

    @Test
    public void include_export_root_and_nested_children()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        createNode( "child1_1_1", child1_1.path() );
        createNode( "child1_1_2", child1_1.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.create( "/mynode/child1" ).build() ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 4, result.getExportedNodes().getSize() );

        assertFileExists( "myExport/child1/_/node.xml" );
        assertFileExists( "myExport/child1/child1_1/_/node.xml" );
        assertFileExists( "myExport/child1/child1_1/child1_1_1/_/node.xml" );
        assertFileExists( "myExport/child1/child1_1/child1_1_2/_/node.xml" );
    }

    @Disabled // Wait with this until decided how to handle versions. Only in dump, or in export too?
    @Test
    public void create_binary_files()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "image1.jpg" );
        final BinaryReference binaryRef2 = BinaryReference.from( "image2.jpg" );
        data.addBinaryReference( "my-image-1", binaryRef1 );
        data.addBinaryReference( "my-image-2", binaryRef2 );

        this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( "this-is-the-binary-data-for-image1".getBytes() ) ).
            attachBinary( binaryRef2, ByteSource.wrap( "this-is-the-binary-data-for-image2".getBytes() ) ).
            build() );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 2, result.getExportedNodes().getSize() );
        assertEquals( 2, result.getExportedBinaries().size() );

        assertFileExists( "myExport/_/node.xml" );
        assertFileExists( "myExport/my-node/_/node.xml" );
        assertFileExists( "myExport/my-node/_/bin/image1.jpg" );
        assertFileExists( "myExport/my-node/_/bin/image2.jpg" );
    }

    @Test
    public void export_properties()
        throws Exception
    {
        NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertFileDoesNotExist( "myExport/export.properties" );

        NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            xpVersion( "X.Y.Z-SNAPSHOT" ).
            rootDirectory( this.temporaryFolder.resolve( "myRoot" ) ).
            targetDirectory( this.temporaryFolder.resolve( "myRoot" ).resolve( "myExport" ) ).
            build().
            execute();

        assertFileExists( "myRoot/export.properties" );
    }

    @Test
    public void one_node_error()
        throws Exception
    {
        createNode( "mynode", NodePath.ROOT );

        final ExportWriter exportWriter = Mockito.mock( ExportWriter.class );
        Mockito.doThrow( new RuntimeException( "exception message" ) ).when( exportWriter ).writeElement( Mockito.isA( Path.class ),
                                                                                                          Mockito.anyString() );

        final NodeExportResult result = NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( exportWriter ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            build().
            execute();

        assertEquals( 1, result.getExportErrors().size() );
        assertEquals( "java.lang.RuntimeException: exception message", result.getExportErrors().get( 0 ).toString() );
    }

    // Asserts and Utils

    private NodeExportResult doExportRoot( final boolean exportVersions )
    {
        return NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( Paths.get( this.temporaryFolder.toString(), "myExport" ) ).
            exportVersions( exportVersions ).
            build().
            execute();
    }

    private void assertExported( final Node node )
    {
        final String expectedFilePath = "myExport" + node.path() + "/" + SYSTEM_FOLDER_NAME + "/" + NODE_XML_EXPORT_NAME;
        assertFileExists( expectedFilePath );
    }

    private void assertVersionExported( final Node exportedNode, final Node exportedVersion )
    {
        final String baseFolder = getBaseFolder( exportedNode );
        final String versionsFolder = baseFolder + VERSION_FOLDER + "/";
        final String versionsDataFolder = versionsFolder + exportedVersion.getNodeVersionId() + "/";
        assertFileExists( versionsDataFolder + exportedVersion.name() + "/" + NODE_XML_EXPORT_NAME );
    }

    private void assertBinaryExported( final Node node, final BinaryReference ref )
    {
        final String baseFolder = getBaseFolder( node );
        assertFileExists( Paths.get( baseFolder, BINARY_FOLDER, ref.toString() ).toString() );
    }

    private void assertVersionBinaryExported( final Node exportedNode, final Node exportedVersion, final BinaryReference ref )
    {
        final String baseFolder = getBaseFolder( exportedNode );
        final String versionsFolder = baseFolder + VERSION_FOLDER + "/";
        final String versionsDataFolder = versionsFolder + exportedVersion.getNodeVersionId() + "/";
        assertFileExists( Paths.get( versionsDataFolder, exportedVersion.name().toString(), BINARY_FOLDER, ref.toString() ).toString() );
    }

    private String getBaseFolder( final Node node )
    {
        return "myExport" + node.path() + "/" + SYSTEM_FOLDER_NAME + "/";
    }

    private void printPaths()
    {
        final File file = this.temporaryFolder.toFile();

        doPrintPaths( file );
    }

    private void doPrintPaths( final File file )
    {
        if ( file.isDirectory() )
        {
            final File[] children = file.listFiles();

            for ( final File child : children )
            {
                doPrintPaths( child );
            }
        }
        else
        {
            System.out.println( file.toPath() );
        }
    }

    private Node createNode( final String name, final NodePath root )
    {
        final Node node = Node.create().
            name( NodeName.from( name ) ).
            parentPath( root ).
            build();

        return this.nodeService.create( CreateNodeParams.from( node ).build() );
    }

    private void assertFileExists( final String path )
    {
        assertThat( temporaryFolder.resolve( path ) ).exists();
    }

    private void assertFileDoesNotExist( final String path )
    {
        assertThat( temporaryFolder.resolve( path ) ).doesNotExist();
    }

}
