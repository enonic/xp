package com.enonic.wem.export.internal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.reader.FileExportReader;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class NodeImporterTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void export_import()
        throws Exception
    {
        final NodeServiceMock exportNodeService = new NodeServiceMock();

        final Node node = createNode( "mynode", NodePath.ROOT, exportNodeService );

        final NodeExportResult result = BatchedNodeExporter.create().
            nodeService( exportNodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult nodeImportResult = NodeImporter.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 1, nodeImportResult.importedNodes.getSize() );
        compareExportImportNodes( node, getImportedNode( importNodeService, "mynode", NodePath.ROOT ) );
    }

    @Test
    public void export_import_with_children()
        throws Exception
    {
        final NodeServiceMock exportNodeService = new NodeServiceMock();

        final Node root = createNode( "mynode", NodePath.ROOT, exportNodeService );
        final Node child1 = createNode( "child1", root.path(), exportNodeService );
        createNode( "child1_1", child1.path(), exportNodeService );
        final Node child1_2 = createNode( "child1_2", child1.path(), exportNodeService );
        createNode( "child1_2_1", child1_2.path(), exportNodeService );
        createNode( "child1_2_2", child1_2.path(), exportNodeService );
        final Node child2 = createNode( "child2", root.path(), exportNodeService );
        createNode( "child2_1", child2.path(), exportNodeService );

        BatchedNodeExporter.create().
            nodeService( exportNodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportRootNode( NodePath.ROOT ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            exportHomePath( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            export();

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult nodeImportResult = NodeImporter.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 8, nodeImportResult.importedNodes.getSize() );

        compareExportImportNodes( root, getImportedNode( importNodeService, "mynode", NodePath.ROOT ) );
        compareExportImportNodes( child1, getImportedNode( importNodeService, "child1", root.path() ) );
        compareExportImportNodes( child1_2, getImportedNode( importNodeService, "child1_2", child1.path() ) );
        compareExportImportNodes( child2, getImportedNode( importNodeService, "child2", root.path() ) );
    }

    @Test
    public void import_into_other_path()
        throws Exception
    {

    }


    @Test
    public void import_node_already_exist()
        throws Exception
    {

    }

    private Node getImportedNode( final NodeServiceMock importNodeService, final String name, final NodePath parent )
    {
        return importNodeService.getByPath( NodePath.newNodePath( parent, name ).build() );
    }

    private Node createNode( final String name, final NodePath parentPath, final NodeService nodeService )
    {
        final Node node = Node.newNode().
            name( NodeName.from( name ) ).
            parent( parentPath ).
            build();

        return nodeService.create( CreateNodeParams.from( node ).build() );
    }

    private void compareExportImportNodes( final Node exported, final Node imported )
    {
        assertNotNull( imported );
        assertEquals( exported.name(), imported.name() );
        assertEquals( exported.path(), imported.path() );
        assertEquals( exported.getChildOrder(), imported.getChildOrder() );
        assertEquals( exported.data(), imported.data() );
    }

}