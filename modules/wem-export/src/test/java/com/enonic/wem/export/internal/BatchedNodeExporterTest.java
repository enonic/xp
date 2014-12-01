package com.enonic.wem.export.internal;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.writer.ExportItemPath;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.writer.VerifiableExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class BatchedNodeExporterTest
{
    private NodeService nodeService;

    private VerifiableExportWriter exportWriter;

    private ExportItemPath exportHome;

    private final String exportName = "node";

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = new NodeServiceMock();
        this.exportWriter = new VerifiableExportWriter();
        this.exportHome = ExportItemPath.from( "exports" );
    }

    @Test
    public void one_node()
        throws Exception
    {
        final Node node = createNode( "mynode", NodePath.ROOT );

        this.nodeService.create( CreateNodeParams.from( node ).build() );

        final NodeExportResult result = BatchedNodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( exportWriter ).
            nodePath( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHome( exportHome ).
            exportName( exportName ).
            build().
            export();

        assertEquals( 1, result.size() );

        verifyNodeExported( node );
    }

    @Test
    public void children_nodes()
        throws Exception
    {
        final Node root = createNode( "mynode", NodePath.ROOT );
        final Node child1 = createNode( "child1", root.path() );
        final Node child1_1 = createNode( "child1_1", child1.path() );
        final Node child1_2 = createNode( "child1_2", child1.path() );
        final Node child1_3 = createNode( "child1_3", child1.path() );
        final Node child1_4 = createNode( "child1_4", child1.path() );
        final Node child1_4_1 = createNode( "child1_4_1", child1_4.path() );
        final Node child1_4_2 = createNode( "child1_4_2", child1_4.path() );
        final Node child2 = createNode( "child2", root.path() );
        final Node child2_1 = createNode( "child2_1", child2.path() );

        final NodeExportResult result = BatchedNodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( this.exportWriter ).
            nodePath( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHome( this.exportHome ).
            exportName( exportName ).
            build().
            export();

        assertEquals( 10, result.size() );

        verifyNodeExported( root );
        verifyNodeExported( child1 );
        verifyNodeExported( child1_1 );
        verifyNodeExported( child1_2 );
        verifyNodeExported( child1_3 );
        verifyNodeExported( child1_4 );
        verifyNodeExported( child1_4_1 );
        verifyNodeExported( child1_4_2 );
        verifyNodeExported( child2 );
        verifyNodeExported( child2_1 );
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

        final Node child1 = createNode( "child1", root.path() );
        createNode( "child2", root.path() );
        createNode( "child3", root.path() );
        createNode( "child4", root.path() );
        createNode( "child5", root.path() );
        createNode( "child6", root.path() );

        BatchedNodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( this.exportWriter ).
            nodePath( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHome( this.exportHome ).
            exportName( exportName ).
            build().
            export();

        final Map<ExportItemPath, String> exportedItems = this.exportWriter.getExportedItems();

        assertTrue( exportedItems.containsKey( getOrderListPath( root ) ) );
        assertFalse( exportedItems.containsKey( getOrderListPath( child1 ) ) );
    }

    private Node createNode( final String name, final NodePath root )
    {
        final Node node = Node.newNode().
            name( NodeName.from( name ) ).
            parent( root ).
            build();

        return this.nodeService.create( CreateNodeParams.from( node ).build() );
    }

    private ExportItemPath getExportNodeDataPath( final Node root )
    {
        final ExportItemPath rootExportPath = NodeExportPathResolver.resolveExportRoot( exportHome, exportName );
        final ExportItemPath nodeBasePath = NodeExportPathResolver.resolveExportNodeRoot( rootExportPath, root );
        return NodeExportPathResolver.resolveExportNodeDataPath( nodeBasePath );
    }

    private ExportItemPath getOrderListPath( final Node node )
    {
        return NodeExportPathResolver.resolveOrderListPath( getExportNodeDataPath( node ) );
    }

    private void verifyNodeExported( final Node node )
    {
        assertTrue( this.exportWriter.getExportedItems().containsKey(
            NodeExportPathResolver.resolveNodeXmlPath( getExportNodeDataPath( node ) ) ) );
    }
}