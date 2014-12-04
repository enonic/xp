package com.enonic.wem.export.internal;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.reader.FileExportReader;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class NodeImportCommandTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = ImportNodeException.class)
    public void import_node_non_existing_parent()
        throws Exception
    {
        final NodePath importRoot = NodePath.newNodePath( NodePath.ROOT, "non-existing-node" ).build();

        final NodeServiceMock importNodeService = new NodeServiceMock();

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( importRoot ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();
    }

    @Test
    public void import_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath() + "/myExport/mynode/_" ) );

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( "node.xml" ).getBytes() );

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 1, nodeImportResult.importedNodes.getSize() );
    }

    @Test
    public void import_nodes()
        throws Exception
    {
        createNodeFile( "/myExport/mynode/_" );
        createNodeFile( "/myExport/mynode/mychild/_" );
        createNodeFile( "/myExport/mynode/mychild/mychildchild/_" );
        createNodeFile( "/myExport/mynode/mychild/mychildchild/mychildchildchild/_" );

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 4, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( importNodeService, NodePath.ROOT, "mynode" );
        final Node mychild = assertNodeExists( importNodeService, mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( importNodeService, mychild.path(), "mychildchild" );
        assertNodeExists( importNodeService, mychildchild.path(), "mychildchildchild" );
    }

    @Test
    public void import_nodes_into_child()
        throws Exception
    {

        createNodeFile( "/myExport/mynode/_" );
        createNodeFile( "/myExport/mynode/mychild/_" );
        createNodeFile( "/myExport/mynode/mychild/mychildchild/_" );
        createNodeFile( "/myExport/mynode/mychild/mychildchild/mychildchildchild/_" );

        final NodePath importRoot = NodePath.newNodePath( NodePath.ROOT, "my-import-here" ).build();

        final NodeServiceMock importNodeService = new NodeServiceMock();

        importNodeService.create( CreateNodeParams.create().
            parent( importRoot.getParentPath() ).
            name( importRoot.getLastElement().toString() ).
            build() );

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( importRoot ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 4, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( importNodeService, importRoot, "mynode" );
        final Node mychild = assertNodeExists( importNodeService, mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( importNodeService, mychild.path(), "mychildchild" );
        assertNodeExists( importNodeService, mychildchild.path(), "mychildchildchild" );
    }

    private Node assertNodeExists( final NodeServiceMock importNodeService, final NodePath parentPath, final String name )
    {
        final Node node = importNodeService.getByPath( NodePath.newNodePath( parentPath, name ).build() );
        assertNotNull( node );
        return node;
    }

    private void createNodeFile( final String path )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath() + path ) );
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( "node.xml" ).getBytes() );
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