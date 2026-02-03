package com.enonic.xp.core.export;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.export.NodeExporter;
import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeExportIntegrationTest
    extends AbstractNodeTest
{
    private static final String EXPORT_NAME = "myExport";

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void single_node()
        throws IOException
    {
        final Node myNode = createNode( NodePath.ROOT, "myNode" );

        final NodeExportResult result = doExportRoot();

        assertEquals( 2, result.size() );
        assertExported( myNode );
    }

    @Test
    void single_node_with_binary()
        throws IOException
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

        final NodeExportResult result = doExportRoot();

        assertEquals( 2, result.size() );
        assertEquals( 1, result.getExportedBinaries().size() );
        assertExported( myNode );
        assertBinaryExported( myNode, binaryRef );
    }

    @Test
    void single_node_multiple_versions()
        throws IOException
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.addInstant( "myInstant", Instant.now() ) ).
            build() );

        final NodeExportResult result = doExportRoot();

        assertEquals( 2, result.size() );

        printPaths();

        assertExported( updatedNode );
    }

    @Test
    void single_node_changed_name()
        throws IOException
    {
        final Node originalNode = createNode( NodePath.ROOT, "initial-name" );

        final Node renamedNode =
            this.nodeService.move( MoveNodeParams.create().nodeId( originalNode.id() ).newName( NodeName.from( "new-node-name" ) ).build() )
                .getMovedNodes()
                .getFirst()
                .getNode();

        final NodeExportResult result = doExportRoot();

        assertEquals( 2, result.size() );

        printPaths();
        assertExported( renamedNode );
    }

    @Test
    void single_node_with_binary_changed()
        throws IOException
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

        final NodeExportResult result = doExportRoot();

        printPaths();

        assertEquals( 2, result.size() );
        assertExported( myNode );
        assertBinaryExported( updatedNode, binaryRefUpdated );
    }

    @Test
    void one_node_file()
        throws IOException
    {
        createNode( NodePath.ROOT, "mynode" );

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 2, result.size() );

        assertZipEntryExists( EXPORT_NAME + "/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/_/node.xml" );
    }

    @Test
    void children_nodes()
        throws IOException
    {
        final Node root = createNode( NodePath.ROOT, "mynode" );
        final Node child1 = createNode( root.path(), "child1" );
        createNode( child1.path(), "child1_1" );
        final Node child1_2 = createNode( child1.path(), "child1_2" );
        createNode( child1_2.path(), "child1_2_1" );
        createNode( child1_2.path(), "child1_2_2" );
        final Node child2 = createNode( root.path(), "child2" );
        createNode( child2.path(), "child2_1" );

        final NodeExportListener nodeExportListener = Mockito.mock( NodeExportListener.class );

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .nodeExportListener( nodeExportListener )
                .build()
                .execute();
        }

        assertEquals( 9, result.size() );

        assertZipEntryExists( EXPORT_NAME + "/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child1/child1_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child1/child1_2/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child1/child1_2/child1_2_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child1/child1_2/child1_2_2/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child2/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/mynode/child2/child2_1/_/node.xml" );

        Mockito.verify( nodeExportListener ).nodeResolved( 9 );
        Mockito.verify( nodeExportListener, Mockito.times( 9 ) ).nodeExported( 1 );
    }

    @Test
    void writerOrderList()
        throws IOException
    {
        final Node root =
            Node.create().name( NodeName.from( "root" ) ).parentPath( NodePath.ROOT ).childOrder( ChildOrder.manualOrder() ).build();

        createNode( CreateNodeParams.from( root ).build() );

        createNode( root.path(), "child1" );
        createNode( root.path(), "child2" );
        createNode( root.path(), "child3" );
        createNode( root.path(), "child4" );
        createNode( root.path(), "child5" );
        createNode( root.path(), "child6" );

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 8, result.size() );

        assertZipEntryExists( EXPORT_NAME + "/root/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/root/_/manualChildOrder.txt" );
    }


    @Test
    void export_from_child_of_child()
        throws IOException
    {
        final Node root = createNode( NodePath.ROOT, "mynode" );
        final Node child1 = createNode( root.path(), "child1" );
        final Node child1_1 = createNode( child1.path(), "child1_1" );
        createNode( child1_1.path(), "child1_1_1" );
        createNode( child1_1.path(), "child1_1_2" );

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( new NodePath( "/mynode/child1/child1_1" ) )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 3, result.getExportedNodes().getSize() );

        assertZipEntryExists( EXPORT_NAME + "/child1_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/child1_1/child1_1_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/child1_1/child1_1_2/_/node.xml" );
    }

    @Test
    void include_export_root_and_nested_children()
        throws IOException
    {
        final Node root = createNode( NodePath.ROOT, "mynode" );
        final Node child1 = createNode( root.path(), "child1" );
        createNode( root.path(), "child2" );
        final Node child1_1 = createNode( child1.path(), "child1_1" );
        createNode( child1_1.path(), "child1_1_1" );
        createNode( child1_1.path(), "child1_1_2" );

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( new NodePath( "/mynode/child1" ) )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 4, result.getExportedNodes().getSize() );

        assertZipEntryExists( EXPORT_NAME + "/child1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/child1/child1_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/child1/child1_1/child1_1_1/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/child1/child1_1/child1_1_2/_/node.xml" );
    }

    @Test
    void create_binary_files()
        throws IOException
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

        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 2, result.getExportedNodes().getSize() );
        assertEquals( 2, result.getExportedBinaries().size() );

        assertZipEntryExists( EXPORT_NAME + "/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/my-node/_/node.xml" );
        assertZipEntryExists( EXPORT_NAME + "/my-node/_/bin/image1.jpg" );
        assertZipEntryExists( EXPORT_NAME + "/my-node/_/bin/image2.jpg" );
    }

    @Test
    void export_properties()
        throws IOException
    {
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .xpVersion( "X.Y.Z-SNAPSHOT" )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .build()
                .execute();
        }

        assertZipEntryExists( EXPORT_NAME + "/export.properties" );
    }

    @Test
    void one_node_error_with_root()
    {
        createNode( NodePath.ROOT, "mynode" );
        refresh();

        final ExportWriter exportWriter = Mockito.mock( ExportWriter.class );

        // Allow export.properties to be written, but throw exception for node.xml
        Mockito.doAnswer( invocation -> {
            Path path = invocation.getArgument( 0 );
            if ( path.toString().endsWith( "export.properties" ) )
            {
                return null; // Allow export.properties
            }
            throw new RuntimeException( "exception message" );
        } ).when( exportWriter ).writeElement( Mockito.isA( Path.class ), Mockito.anyString() );

        final NodeExportResult result = NodeExporter.create().nodeService( this.nodeService )
            .nodeExportWriter( exportWriter )
            .sourceNodePath( NodePath.ROOT )
            .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
            .xpVersion( "1.0.0" )
            .build()
            .execute();
        assertEquals( 2, result.getExportErrors().size() );
        assertEquals( "java.lang.RuntimeException: exception message", result.getExportErrors().get( 0 ).toString() );
    }

    @Test
    void testRootNotFound()
        throws IOException
    {
        final NodeExportResult result;
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            result = NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.create().addElement( "unknown" ).build() )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }

        assertEquals( 0, result.size() );
        assertEquals( "Node with path '/unknown' not found in branch 'draft', nothing to export", result.getExportErrors().getFirst().toString() );
    }

    // Asserts and Utils

    private NodeExportResult doExportRoot()
        throws IOException
    {
        try (ZipExportWriter exportWriter = ZipExportWriter.create( this.temporaryFolder, EXPORT_NAME ))
        {
            return NodeExporter.create()
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .sourceNodePath( NodePath.ROOT )
                .targetDirectory( this.temporaryFolder.resolve( EXPORT_NAME ) )
                .xpVersion( "1.0.0" )
                .build()
                .execute();
        }
    }

    private void assertExported( final Node node )
        throws IOException
    {
        final String entryPath = EXPORT_NAME + node.path().toString() + "/" + NodeExportPathResolver.SYSTEM_FOLDER_NAME + "/" +
            NodeExportPathResolver.NODE_XML_EXPORT_NAME;
        assertZipEntryExists( entryPath );
    }

    private void assertBinaryExported( final Node node, final BinaryReference ref )
        throws IOException
    {
        final String entryPath = EXPORT_NAME + node.path().toString() + "/" + NodeExportPathResolver.SYSTEM_FOLDER_NAME + "/" +
            NodeExportPathResolver.BINARY_FOLDER + "/" + ref.toString();
        assertZipEntryExists( entryPath );
    }

    private void printPaths()
        throws IOException
    {
        final Path zipPath = temporaryFolder.resolve( EXPORT_NAME + ".zip" );
        if ( !zipPath.toFile().exists() )
        {
            System.out.println( "Zip file does not exist: " + zipPath );
            return;
        }
        try (ZipFile zipFile = new ZipFile( zipPath.toFile() ))
        {
            zipFile.stream().forEach( entry -> System.out.println( entry.getName() ) );
        }
    }

    private void assertZipEntryExists( final String entryPath )
        throws IOException
    {
        final Set<String> entries = getZipEntries();
        assertTrue( entries.contains( entryPath ), "Expected entry '" + entryPath + "' not found in zip. Entries: " + entries );
    }

    private Set<String> getZipEntries()
        throws IOException
    {
        final Path zipPath = temporaryFolder.resolve( EXPORT_NAME + ".zip" );
        final Set<String> entryNames = new HashSet<>();
        try (ZipFile zipFile = new ZipFile( zipPath.toFile() ))
        {
            zipFile.stream().forEach( entry -> entryNames.add( entry.getName() ) );
        }
        return entryNames;
    }

}
