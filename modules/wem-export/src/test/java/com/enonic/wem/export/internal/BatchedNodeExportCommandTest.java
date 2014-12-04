package com.enonic.wem.export.internal;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class BatchedNodeExportCommandTest
{
    private NodeService nodeService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before

    public void setUp()
        throws Exception
    {
        this.nodeService = new NodeServiceMock();
    }

    @Test
    public void one_node_file()
        throws Exception
    {
        final Node node = createNode( "mynode", NodePath.ROOT );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        assertEquals( 1, result.size() );

        assertFileExists( "/myExport/mynode/_/node.xml" );
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

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        assertEquals( 8, result.size() );

        assertFileExists( "/myExport/mynode/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/child1_2_1/_/node.xml" );
        assertFileExists( "/myExport/mynode/child1/child1_2/child1_2_2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child2/_/node.xml" );
        assertFileExists( "/myExport/mynode/child2/child2_1/_/node.xml" );
    }

    @Test
    public void writerOrderList()
    {
        final Node root = Node.newNode().
            name( NodeName.from( "root" ) ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            build();

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        createNode( "child3", root.path() );
        createNode( "child4", root.path() );
        createNode( "child5", root.path() );
        createNode( "child6", root.path() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        assertEquals( 7, result.size() );

        assertFileExists( "/myExport/root/_/node.xml" );
        assertFileExists( "/myExport/root/_/manualChildOrder.txt" );
    }


    @Test
    public void relative_path_from_export_root_node()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        createNode( "child1_1_1", child1_1.path() );
        createNode( "child1_1_2", child1_1.path() );

        this.nodeService.create( CreateNodeParams.from( root ).build() );

        final NodeExportResult result = BatchedNodeExportCommand.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.newPath( "/mynode/child1/child1_1" ).build() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        assertEquals( 2, result.size() );

        assertFileExists( "/myExport/child1_1_1/_/node.xml" );
        assertFileExists( "/myExport/child1_1_2/_/node.xml" );
    }

    private Node createNode( final String name, final NodePath root )
    {
        final Node node = Node.newNode().
            name( NodeName.from( name ) ).
            parent( root ).
            build();

        return this.nodeService.create( CreateNodeParams.from( node ).build() );
    }

    private void assertFileExists( final String node1Path )
    {
        assertTrue( new File( this.temporaryFolder.getRoot().getPath() + node1Path ).exists() );
    }

}