package com.enonic.xp.core.impl.export;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.export.ImportNodeException;
import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.Assert.*;

public class NodeImporterTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private NodeService importNodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.importNodeService = new NodeServiceMock();
        createRootNodeXmlFile( Paths.get( "myExport" ) );
    }

    @Test
    public void import_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), "myExport", "mynode", "_" ) );
        assert nodeFileDir != null;

        final byte[] nodeXmlFile = readFromFile( "node_unordered.xml" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );

        final NodeImportResult result = NodeImporter.create().
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 2, result.addedNodes.getSize() );
    }

    @Test
    public void import_node_with_timestamp()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), "myExport", "mynode", "_" ) );
        assert nodeFileDir != null;

        final byte[] nodeXmlFile = readFromFile( "node_timestamp.xml" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );

        final NodeImportResult result = NodeImporter.create().
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 2, result.addedNodes.getSize() );
    }

    @Test
    public void import_node_with_id()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), "myExport", "mynode", "_" ) );
        assert nodeFileDir != null;

        final byte[] nodeXmlFile = readFromFile( "node_with_id_1234.xml" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );

        final NodeImportResult result = NodeImporter.create().
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 2, result.addedNodes.getSize() );

        final Node node1234 = importNodeService.getById( NodeId.from( "1234" ) );
        assertNotNull( node1234 );
    }

    @Test
    public void import_update_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), "myExport", "mynode", "_" ) );
        assert nodeFileDir != null;

        final byte[] nodeXmlFile = readFromFile( "node_unordered.xml" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult result = NodeImporter.create().
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 2, result.addedNodes.getSize() );

        final NodeImportResult updateResult = NodeImporter.create().
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, updateResult.getImportErrors().size() );
        assertEquals( 0, updateResult.addedNodes.getSize() );
        assertEquals( 2, updateResult.updateNodes.getSize() );
    }


    @Test
    public void import_nodes()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild" ), false );

        NodeImportListener nodeImportListener = Mockito.mock( NodeImportListener.class );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            nodeImportListener( nodeImportListener ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 5, result.addedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mychildchild.path(), "mychildchildchild" );

        Mockito.verify( nodeImportListener ).nodeResolved( 5L );
        Mockito.verify( nodeImportListener, Mockito.times( 5 ) ).nodeImported( 1L );
    }

    @Test
    public void import_nodes_into_child()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild" ), false );

        final NodePath importRoot = NodePath.create( NodePath.ROOT, "my-import-here" ).build();

        this.importNodeService.create( CreateNodeParams.create().
            parent( importRoot.getParentPath() ).
            name( importRoot.getLastElement().toString() ).
            build() );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( importRoot ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 5, result.addedNodes.getSize() );

        final Node mynode = assertNodeExists( importRoot, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mychildchild.path(), "mychildchildchild" );
    }


    @Test(expected = ImportNodeException.class)
    public void import_node_non_existing_parent()
        throws Exception
    {
        final NodePath importRoot = NodePath.create( NodePath.ROOT, "non-existing-node" ).build();

        NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( importRoot ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();
    }

    @Test
    public void expect_order_file_if_manual()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode", "_" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 1, result.getImportErrors().size() );
        assertEquals( 1, result.getAddedNodes().getSize() );
    }

    @Test
    public void continue_on_error()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode2" ), true );
        createNodeXmlFile( Paths.get( "myExport", "mynode2", "mychild1" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode2", "mychild1", "mychildchild1" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode3" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 6, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getImportErrors().size() );
    }

    @Test
    public void import_nodes_ordered()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ), true );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1", "mychildchild" ), true );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild1" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild2" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild3" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild2" ), false );

        createOrderFile( Paths.get( "myExport", "mynode" ), "mychild2", "mychild1" );
        createOrderFile( Paths.get( "myExport", "mynode", "mychild1", "mychildchild" ), "mychildchildchild1", "mychildchildchild2",
                         "mychildchildchild3" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 8, result.addedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        assertNull( mynode.getManualOrderValue() );
        final Node myChild1 = assertNodeExists( mynode.path(), "mychild1" );
        assertNotNull( "manualOrderValue should be set", myChild1.getManualOrderValue() );
        final Node myChild2 = assertNodeExists( mynode.path(), "mychild2" );
        assertNotNull( "manualOrderValue should be set", myChild2.getManualOrderValue() );

        final Node mychildchild = assertNodeExists( myChild1.path(), "mychildchild" );
        assertNodeExists( mynode.path(), "mychild2" );
        assertNodeExists( mychildchild.path(), "mychildchildchild1" );
        assertNodeExists( mychildchild.path(), "mychildchildchild2" );
        assertNodeExists( mychildchild.path(), "mychildchildchild3" );
    }

    @Test
    public void ordered_not_in_list_not_imported()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ), true );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild1" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild2" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild3" ), false );

        createOrderFile( Paths.get( "myExport", "mynode" ), "mychild2", "mychild1" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 4, result.addedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        assertNodeExists( mynode.path(), "mychild1" );
        assertNodeExists( mynode.path(), "mychild2" );

        final Node mychild3 = this.importNodeService.getByPath( NodePath.create( mynode.path(), "mychild3" ).build() );
        assertNull( mychild3 );
    }

    @Test
    public void import_with_binaries_missing_file()
        throws Exception
    {
        createNodeXmlFileWithBinaries( Paths.get( "myExport", "mynode" ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 1, result.getImportErrors().size() );
        assertEquals( 2, result.getAddedNodes().getSize() );
    }

    @Test
    public void import_with_binary()
        throws Exception
    {
        createNodeXmlFileWithBinaries( Paths.get( "myExport", "mynode" ) );
        createBinaryFile( Paths.get( "myExport", "mynode" ), "image.jpg", "this-is-the-source".getBytes() );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, mynode.getAttachedBinaries().getSize() );
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

        createNodeXmlFile( Paths.get( "myExport", "mynode" ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", myChildName ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", myChildName, myChildChildName ), false );
        createNodeXmlFile( Paths.get( "myExport", "mynode", myChildName, myChildChildName, myChildChildChildName ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.importNodeService ).
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
            nodeService( importNodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            xslt( VirtualFiles.from( xsltFilePath ) ).
            xsltParam( "applicationId", "com.enonic.starter.bootstrap" ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 2, result.addedNodes.getSize() );
        final Node importedNode = this.importNodeService.getByPath( result.getAddedNodes().first() );
        assertNotNull( importedNode );
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

    private void createRootNodeXmlFile( final Path exportPath )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        assert nodeFileDir != null;
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( "root-node.xml" ).getBytes() );
    }

    private void createNodeXmlFile( final Path exportPath, boolean ordered )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        assert nodeFileDir != null;
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( ordered ? "node_manual_ordered.xml" : "node_unordered.xml" ).getBytes() );
    }

    private void createNodeXmlFileWithBinaries( final Path path )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), path.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        assert nodeFileDir != null;
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( "node_with_binary.xml" ).getBytes() );
    }


    private Node assertNodeExists( final NodePath parentPath, final String name )
    {
        final Node node = importNodeService.getByPath( NodePath.create( parentPath, name ).build() );
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