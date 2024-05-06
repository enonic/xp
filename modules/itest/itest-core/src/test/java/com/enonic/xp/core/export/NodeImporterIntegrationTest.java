package com.enonic.xp.core.export;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.export.NodeImporter;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.export.ImportNodeException;
import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeImporterIntegrationTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        copyFormResource( "root-node.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );
    }

    @Test
    public void import_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", "mynode", "_" ) );

        copyFormResource( "node_unordered.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 1, result.getAddedNodes().getSize() );
    }

    @Test
    public void import_node_with_timestamp()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", "mynode", "_" ) );

        copyFormResource( "node_timestamp.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
    }

    @Test
    public void import_node_with_id()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", "mynode", "_" ) );

        copyFormResource( "node_with_id_1234.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 1, result.getAddedNodes().getSize() );

        final Node node1234 = nodeService.getById( NodeId.from( "1234" ) );
        assertNotNull( node1234 );
    }

    @Test
    public void import_update_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", "mynode", "_" ) );

        copyFormResource( "node_unordered.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

        final NodeImportResult updateResult = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, updateResult.getImportErrors().size() );
        assertEquals( 0, updateResult.getAddedNodes().getSize() );
        assertEquals( 2, updateResult.getUpdateNodes().getSize() );
    }


    @Test
    public void import_nodes()
        throws Exception
    {
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild", "mychildchild" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild" ), false );

        NodeImportListener nodeImportListener = Mockito.mock( NodeImportListener.class );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            nodeImportListener( nodeImportListener ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 4, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

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
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild", "mychildchild" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild" ), false );

        final NodePath importRoot = new NodePath( "/my-import-here" );

        this.nodeService.create( CreateNodeParams.create().
            parent( importRoot.getParentPath() ).
            name( importRoot.getName() ).
            build() );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( importRoot ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 4, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

        final Node mynode = assertNodeExists( importRoot, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mychildchild.path(), "mychildchildchild" );
    }


    @Test
    public void import_node_non_existing_parent()
    {
        final NodePath importRoot = new NodePath( "/non-existing-node" );

        assertThrows( ImportNodeException.class, () -> NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( importRoot ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute() );
    }

    @Test
    public void expect_order_file_if_manual()
        throws Exception
    {
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "_" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 1, result.getImportErrors().size() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 0, result.getAddedNodes().getSize() );
    }

    @Test
    public void continue_on_error()
        throws Exception
    {
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode2" ), true );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode2", "mychild1" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode2", "mychild1", "mychildchild1" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode3" ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 5, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 1, result.getImportErrors().size() );
    }

    @Test
    public void import_nodes_ordered()
        throws Exception
    {
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), true );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1", "mychildchild" ), true );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild1" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild2" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1", "mychildchild", "mychildchildchild3" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild2" ), false );

        createOrderFile( resolveInTemporaryFolder( "myExport", "mynode" ), "mychild2", "mychild1" );
        createOrderFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1", "mychildchild" ), "mychildchildchild1",
                         "mychildchildchild2", "mychildchildchild3" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 7, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        assertNull( mynode.getManualOrderValue() );
        final Node myChild1 = assertNodeExists( mynode.path(), "mychild1" );
        assertNotNull( myChild1.getManualOrderValue(), "manualOrderValue should be set" );
        final Node myChild2 = assertNodeExists( mynode.path(), "mychild2" );
        assertNotNull( myChild2.getManualOrderValue(), "manualOrderValue should be set" );

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
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), true );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild1" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild2" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", "mychild3" ), false );

        createOrderFile( resolveInTemporaryFolder( "myExport", "mynode" ), "mychild2", "mychild1" );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 3, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        assertNodeExists( mynode.path(), "mychild1" );
        assertNodeExists( mynode.path(), "mychild2" );

        final Node mychild3 = this.nodeService.getByPath( new NodePath( mynode.path(), NodeName.from( "mychild3" ) ) );
        assertNull( mychild3 );
    }

    @Test
    public void import_with_binaries_missing_file()
        throws Exception
    {
        createNodeXmlFileWithBinaries( resolveInTemporaryFolder( "myExport", "mynode" ) );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 2, result.getImportErrors().size() );
        assertEquals( 0, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
    }

    @Test
    public void import_with_binary()
        throws Exception
    {
        createNodeXmlFileWithBinaries( resolveInTemporaryFolder( "myExport", "mynode" ) );
        createBinaryFile( resolveInTemporaryFolder( "myExport", "mynode" ), "image.jpg", "this-is-the-source".getBytes() );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, mynode.getAttachedBinaries().getSize() );
        final AttachedBinary attachedBinary = mynode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "image.jpg" ) );
        assertNotNull( attachedBinary );
        assertNotNull( attachedBinary.getBlobKey() );
    }

    @Test
    public void import_special_characters()
        throws Exception
    {
        final String myChildName = "my child with spaces";
        final String myChildChildName = "åæø";
        final String myChildChildChildName = "êôö-.(%fi_)";

        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode" ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", myChildName ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", myChildName, myChildChildName ), false );
        createNodeXmlFile( resolveInTemporaryFolder( "myExport", "mynode", myChildName, myChildChildName, myChildChildChildName ), false );

        final NodeImportResult result = NodeImporter.create().
            nodeService( this.nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( this.temporaryFolder.resolve( "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 4, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );

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
        final Path nodeFileDir = Files.createDirectories( temporaryFolder.resolve( "myExport" ).resolve( "mynode" ).resolve( "_" ) );
        final Path xsltFilePath = nodeFileDir.resolve( "transform.xsl" );

        copyFormResource( "node_with_appref.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );
        copyFormResource( "transform.xsl", xsltFilePath );

        final NodeImportResult result = NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            xslt( VirtualFiles.from( xsltFilePath ) ).
            xsltParam( "applicationId", "com.enonic.starter.bootstrap" ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 1, result.getAddedNodes().getSize() );
        final Node importedNode = this.nodeService.getByPath( result.getAddedNodes().first() );
        assertNotNull( importedNode );
    }

    @Test
    public void import_node_without_permission_import()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( resolveInTemporaryFolder( "myExport", "mynode", "_" ) );

        copyFormResource( "node_unordered.xml", nodeFileDir.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME ) );

        NodeImporter.create().
            nodeService( nodeService ).
            targetNodePath( NodePath.ROOT ).
            sourceDirectory( VirtualFiles.from( temporaryFolder.resolve( "myExport" ) ) ).
            importPermissions( false ).
            build().
            execute();

        final Node addedNode = nodeService.getByPath( new NodePath( "/mynode" ) );

        assertTrue(
            addedNode.getPermissions().isAllowedFor( PrincipalKey.from( "user:system:test-user" ), Permission.READ, Permission.CREATE,
                                                     Permission.MODIFY, Permission.DELETE, Permission.PUBLISH, Permission.READ_PERMISSIONS,
                                                     Permission.WRITE_PERMISSIONS ) );
    }

    private void createOrderFile( final Path exportPath, final String... childNodeNames )
        throws Exception
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final Path nodeFileDir = exportPath.resolve( NodeExportPathResolver.SYSTEM_FOLDER_NAME );

        StringBuilder builder = new StringBuilder();

        for ( final String childNodeName : childNodeNames )
        {
            builder.append( childNodeName );
            builder.append( lineSeparator );
        }

        Files.writeString( nodeFileDir.resolve( NodeExportPathResolver.ORDER_EXPORT_NAME ), builder.toString() );
    }

    private void createBinaryFile( final Path exportPath, final String fileName, final byte[] bytes )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( exportPath.
            resolve( NodeExportPathResolver.SYSTEM_FOLDER_NAME ).
            resolve( NodeExportPathResolver.BINARY_FOLDER ) );

        Files.write( nodeFileDir.resolve( fileName ), bytes );
    }

    private void createNodeXmlFile( final Path exportPath, boolean ordered )
        throws Exception
    {
        final Path file = Files.createDirectories( exportPath.resolve( NodeExportPathResolver.SYSTEM_FOLDER_NAME ) ).resolve(
            NodeExportPathResolver.NODE_XML_EXPORT_NAME );
        copyFormResource( ordered ? "node_manual_ordered.xml" : "node_unordered.xml", file );
    }

    private void createNodeXmlFileWithBinaries( final Path nodeFileDir )
        throws Exception
    {
        final Path file = Files.createDirectories( nodeFileDir.resolve( NodeExportPathResolver.SYSTEM_FOLDER_NAME ) ).
            resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME );
        copyFormResource( "node_with_binary.xml", file );
    }

    private Path resolveInTemporaryFolder( String... parts )
    {
        Path resolved = temporaryFolder;
        for ( String part : parts )
        {
            resolved = resolved.resolve( part );
        }
        return resolved;
    }

    private Node assertNodeExists( final NodePath parentPath, final String name )
    {
        final Node node = nodeService.getByPath( new NodePath( parentPath, NodeName.from( name ) ) );
        assertNotNull( node );
        return node;
    }

    final void copyFormResource( final String resourceName, final Path target )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( resourceName ), "Resource file [" + resourceName + "] not found" );
        try (stream)
        {
            Files.copy( stream, target );
        }
    }
}
