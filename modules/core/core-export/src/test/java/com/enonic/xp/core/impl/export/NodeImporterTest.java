package com.enonic.xp.core.impl.export;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.Assert.*;

public class NodeImporterTest
    extends AbstractNodeTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private VirtualFile exportRootFolder;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();

        this.exportRootFolder = VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) );
    }

    @Test
    public void import_node()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 1, 0, 0, result );
        assertNodeExists( "/mynode" );
    }

    @Test
    public void import_as_root()
        throws Exception
    {
        createExportFile( Paths.get( "myExport" ), "root-node.xml" );
        createExportFile( Paths.get( "myExport", "mynode" ), "node_unordered.xml" );

        final NodeImportResult result = NodeHelper.runAsAdmin( () -> NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute() );

        assertAddUpdateError( 1, 1, 0, result );
        assertNodeExists( "/" );
        assertNodeExists( "/mynode" );
    }

    @Test
    public void import_node_with_timestamp_set()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), "node_timestamp.xml" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 1, 0, 0, result );
        assertNodeExists( "/mynode" );

        // Assert same timestamp as in export file
        final Node createdNode = getByPath( "/mynode" );
        assertEquals( Instant.parse( "2014-01-01T10:00:00Z" ), createdNode.getTimestamp() );
    }

    @Test
    public void import_node_with_id()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), "node_with_id_1234.xml" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 1, 0, 0, result );
        assertNodeExists( "/mynode" );

        final Node node1234 = nodeService.getById( NodeId.from( "1234" ) );
        assertNotNull( node1234 );
    }

    @Test
    public void import_update_node()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), "node_unordered.xml" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 1, 0, 0, result );
        assertNodeExists( "/mynode" );

        final NodeImportResult updateResult = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 0, 1, 0, updateResult );
        assertNodeExists( "/mynode" );
    }

    @Test
    public void import_node_tree()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "node1" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "node1", "node1_1" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "node1", "node1_1", "node1_1_1" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 4, result.addedNodes.getSize() );

        assertNodeExists( "/mynode" );
        assertNodeExists( "/mynode/node1" );
        assertNodeExists( "/mynode/node1/node1_1" );
        assertNodeExists( "/mynode/node1/node1_1/node1_1_1" );
    }

    @Test
    public void import_nodes_into_child()
        throws Exception
    {
        final Node existingNode = createNode( NodePath.ROOT, "existingNode" );

        createExportFile( Paths.get( "myExport", "mynode" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "node1" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( existingNode.path() ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 2, 0, 0, result );

        assertNodeExists( "/existingNode/mynode" );
        assertNodeExists( "/existingNode/mynode/node1" );
    }

    @Test
    public void import_node_non_existing_parent()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "mychild" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.create( "/fisk" ).build() ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 0, 0, 2, result );
    }

    @Test
    public void report_error_if_order_file_missing()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), "node_manual_ordered.xml" );
        createExportFile( Paths.get( "myExport", "mynode", "myChild1" ), false );
        createExportFile( Paths.get( "myExport", "mynode", "myChild2" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        for ( NodeImportResult.ImportError error : result.getImportErrors() )
        {
            System.out.println( error );
        }

        // Should report one error since manual order file is missing
        assertAddUpdateError( 3, 0, 1, result );
    }

    @Test
    public void import_nodes_ordered()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "node1" ), true );
        createExportFile( Paths.get( "myExport", "node1", "node1_1" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_1", "node1_1_1" ), true );
        createExportFile( Paths.get( "myExport", "node1", "node1_1", "node1_1_1", "node1_1_1_1" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_1", "node1_1_1", "node1_1_1_2" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_1", "node1_1_1", "node1_1_1_3" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_2" ), false );

        createOrderFile( Paths.get( "myExport", "node1" ), "node1_2", "node1_1" );
        createOrderFile( Paths.get( "myExport", "node1", "node1_1", "node1_1_1" ), "node1_1_1_2", "node1_1_1_3", "node1_1_1_1" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertAddUpdateError( 7, 0, 0, result );

        assertChildOrder( "/node1", "/node1/node1_2", "/node1/node1_1" );
        assertChildOrder( "/node1/node1_1/node1_1_1", //
                          "/node1/node1_1/node1_1_1/node1_1_1_2", //
                          "/node1/node1_1/node1_1_1/node1_1_1_3", //
                          "/node1/node1_1/node1_1_1/node1_1_1_1" );
    }

    @Test
    public void ordered_not_in_list_not_imported()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "node1" ), true );
        createExportFile( Paths.get( "myExport", "node1", "node1_1" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_2" ), false );
        createExportFile( Paths.get( "myExport", "node1", "node1_3" ), false );

        createOrderFile( Paths.get( "myExport", "node1" ), "node1_2", "node1_3" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 3, result.addedNodes.getSize() );

        assertNodeExists( "/node1" );
        assertNodeExists( "/node1/node1_2" );
        assertNodeExists( "/node1/node1_3" );
    }

    @Test
    public void import_with_binaries_missing_file()
        throws Exception
    {
        createNodeXmlFileWithBinaries( Paths.get( "myExport", "mynode" ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        for ( NodeImportResult.ImportError error : result.getImportErrors() )
        {
            System.out.println( error );
        }

        // The binary will fail, also the node pointing to the binary
        assertAddUpdateError( 0, 0, 2, result );
    }

    @Test
    public void import_with_binary()
        throws Exception
    {
        createNodeXmlFileWithBinaries( Paths.get( "myExport", "mynode" ) );
        createBinaryFile( Paths.get( "myExport", "mynode" ), "image.jpg", "this-is-the-source".getBytes() );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( exportRootFolder ).
            build().
            execute();

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );

        assertAddUpdateError( 1, 0, 0, result );

        final AttachedBinary attachedBinary = mynode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "image.jpg" ) );
        assertNotNull( attachedBinary );
        assertNotNull( attachedBinary.getBlobKey() );
    }

    @Ignore
    @Test
    public void import_special_characters()
        throws Exception
    {
        final String myChildName = "my child with spaces";
        final String myChildChildName = "åæø";
        final String myChildChildChildName = "êôö-.(%fi_)";

        createExportFile( Paths.get( "myExport", "mynode" ), false );
        createExportFile( Paths.get( "myExport", "mynode", myChildName ), false );
        createExportFile( Paths.get( "myExport", "mynode", myChildName, myChildChildName ), false );
        createExportFile( Paths.get( "myExport", "mynode", myChildName, myChildChildName, myChildChildChildName ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 5, result.addedNodes.getSize() );

        final Node myNode = assertNodeExists( NodePath.ROOT, "mynode" );
        final Node myChild = assertNodeExists( myNode.path(), myChildName );
        assertEquals( myChildName, myChild.name().toString() );
        final Node myChildChild = assertNodeExists( myChild.path(), myChildChildName );
        assertEquals( myChildChildName, myChildChild.name().toString() );
        final Node myChildChildChild = assertNodeExists( myChildChild.path(), myChildChildChildName );
        assertEquals( myChildChildChildName, myChildChildChild.name().toString() );
    }

    @Test
    public void import_node_with_xslt()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), "myExport", "mynode", "_" ) );
        final Path xsltFilePath = nodeFileDir.resolve( "transform.xsl" );

        final byte[] nodeXmlFile = readFromFile( "node_with_appref.xml" ).getBytes();
        final byte[] xsltFile = readFromFile( "transform.xsl" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );
        Files.write( xsltFilePath, xsltFile );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            xslt( VirtualFiles.from( xsltFilePath ) ).
            xsltParam( "applicationId", "com.enonic.starter.bootstrap" ).
            build().
            execute();

        assertAddUpdateError( 1, 0, 0, result );
        assertNodeExists( "/mynode" );

        final Node importedNode = this.nodeService.getByPath( result.getAddedNodes().first() );
        assertNotNull( importedNode );
    }


    @Test
    public void import_to_manual_ordered_node()
        throws Exception
    {
        createExportFile( Paths.get( "myExport", "mynode" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.create( NodePath.ROOT, "anotherNode" ).build() ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        System.out.println( result );
    }

    private void assertChildOrder( final String parentPath, final String... childPaths )
    {
        final NodeId parentId = getByPath( parentPath ).id();
        assertNotNull( "parent not found", parentId );

        final FindNodesByParentResult children = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( parentId ).
            build() );
        assertEquals( childPaths.length, children.getHits() );

        final Iterator<NodeId> childIterator = children.getNodeIds().iterator();

        for ( final String childPath : childPaths )
        {
            assertEquals( childPath, getNode( childIterator.next() ).path().toString() );
        }
    }

    private void assertAddUpdateError( int added, int updated, int errors, NodeImportResult result )
    {
        assertEquals( "unexpected #errors", errors, result.getImportErrors().size() );
        assertEquals( "unexpected #updated", updated, result.updateNodes.getSize() );
        assertEquals( "unexpected #added", added, result.addedNodes.getSize() );

    }

    private void assertNodeExists( final String path )
    {
        assertNotNull( "Expected node to exists with path [" + path + "]", getByPath( path ) );
    }

    private Node getByPath( final String path )
    {
        return this.nodeService.getByPath( NodePath.create( path ).build() );
    }

    private void createOrderFile( final Path exportPath, final String... childNodeNames )
        throws Exception
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        StringBuilder builder = new StringBuilder();

        for ( final String childNodeName : childNodeNames )
        {
            builder.append( childNodeName );
            builder.append( lineSeparator );
        }

        assert nodeFileDir != null;
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.ORDER_EXPORT_NAME ), builder.toString().getBytes() );
    }

    private void createBinaryFile( final Path exportPath, final String fileName, final byte[] bytes )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME,
                       NodeExportPathResolver.BINARY_FOLDER ) );

        assert nodeFileDir != null;
        Files.write( Paths.get( nodeFileDir.toString(), fileName ), bytes );
    }

    private void createExportFile( final Path exportPath, final String fileName )
        throws Exception
    {
        doCreateExportFile( exportPath, fileName );
    }

    private void createExportFile( final Path exportPath, boolean ordered )
        throws Exception
    {
        final String fileName = ordered ? "node_manual_ordered.xml" : "node_unordered.xml";

        doCreateExportFile( exportPath, fileName );
    }

    private void doCreateExportFile( final Path exportPath, final String fileName )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        assert nodeFileDir != null;

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( fileName ).getBytes() );
    }

    private void createNodeXmlFileWithBinaries( final Path path )
        throws Exception
    {
        doCreateExportFile( path, "node_with_binary.xml" );
    }


    private Node assertNodeExists( final NodePath parentPath, final String name )
    {
        final Node node = nodeService.getByPath( NodePath.create( parentPath, name ).build() );
        assertNotNull( node );
        return node;
    }

    final String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );

    }

}