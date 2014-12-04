package com.enonic.wem.export.internal;

import java.nio.file.Path;

import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.export.internal.writer.ExportWriter;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.mapper.XmlNodeMapper;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

public class BatchedNodeExporter
{
    private final static int DEFAULT_BATCH_SIZE = 100;

    private final NodePath exportRootNode;

    private final int batchSize;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final Path exportTargetPath;

    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private BatchedNodeExporter( final Builder builder )
    {
        exportRootNode = builder.exportRootNode;
        batchSize = builder.batchSize;
        nodeService = builder.nodeService;
        exportWriter = builder.exportWriter;
        xmlNodeSerializer = builder.xmlNodeSerializer;
        this.exportTargetPath = NodeExportPathResolver.resolveExportTargetPath( builder.exportHomePath, builder.exportName );
    }

    public NodeExportResult export()
    {
        final NodeExportResult.Builder resultBuilder = NodeExportResult.create();

        doExportChildNodes( this.exportRootNode, resultBuilder );

        return resultBuilder.build();
    }

    private void doExportChildNodes( final NodePath parentPath, final NodeExportResult.Builder resultBuilder )
    {
        final Node parentNode = nodeService.getByPath( parentPath );

        final double batches = getNumberOfBatches( parentPath );

        int currentFrom = 0;

        final Nodes.Builder allCurrentLevelChildren = Nodes.create();

        for ( int i = 1; i <= batches; i++ )
        {
            final FindNodesByParentResult childrenBatch = exportBatch( parentPath, resultBuilder, currentFrom );

            allCurrentLevelChildren.addAll( childrenBatch.getNodes() );
            currentFrom += this.batchSize;
        }

        writeNodeOrderList( parentNode, allCurrentLevelChildren.build() );
    }

    private FindNodesByParentResult exportBatch( final NodePath nodePath, final NodeExportResult.Builder resultBuilder,
                                                 final int currentFrom )
    {
        final FindNodesByParentResult childrenBatch = nodeService.findByParent( FindNodesByParentParams.create().
            parentPath( nodePath ).
            from( currentFrom ).
            size( this.batchSize ).
            build() );

        for ( final Node child : childrenBatch.getNodes() )
        {
            exportNode( resultBuilder, child );
        }
        return childrenBatch;
    }

    private void exportNode( final NodeExportResult.Builder resultBuilder, final Node child )
    {
        writeNode( child );
        resultBuilder.add( child.id() );
        doExportChildNodes( child.path(), resultBuilder );
    }

    private void writeNode( final Node node )
    {
        final Node relativeNode = Node.newNode( node ).parent( node.parent().removeFromBeginning( this.exportRootNode ) ).build();

        final XmlNode xmlNode = XmlNodeMapper.toXml( relativeNode );

        final String serializedNode = this.xmlNodeSerializer.serialize( xmlNode );

        final Path systemFolder = getNodeDataFolder( node );

        exportWriter.writeElement( NodeExportPathResolver.resolveNodeXmlPath( systemFolder ), serializedNode );
    }

    private void writeNodeOrderList( final Node parent, final Nodes children )
    {
        if ( parent == null || parent.getChildOrder() == null || !parent.getChildOrder().isManualOrder() )
        {
            return;
        }

        final StringBuilder builder = new StringBuilder();

        for ( final Node node : children )
        {
            builder.append( node.name().toString() ).append( LINE_SEPARATOR );
        }

        exportWriter.writeElement( NodeExportPathResolver.resolveOrderListPath( getNodeDataFolder( parent ) ), builder.toString() );
    }

    private double getNumberOfBatches( final NodePath nodePath )
    {
        final FindNodesByParentResult countResult = nodeService.findByParent( FindNodesByParentParams.create().
            countOnly( true ).
            parentPath( nodePath ).
            build() );

        final long totalHits = countResult.getTotalHits();

        return getBatchSize( totalHits );
    }

    private double getBatchSize( final double totalHits )
    {
        return Math.ceil( totalHits / this.batchSize );
    }

    private Path getNodeDataFolder( final Node node )
    {
        final Path nodeBasePath = NodeExportPathResolver.resolveNodeBasePath( this.exportTargetPath, node.path(), exportRootNode );
        return NodeExportPathResolver.resolveNodeDataFolder( nodeBasePath );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodePath exportRootNode;

        private int batchSize = DEFAULT_BATCH_SIZE;

        private NodeService nodeService;

        private ExportWriter exportWriter;

        private XmlNodeSerializer xmlNodeSerializer;

        private Path exportHomePath;

        private String exportName;

        private Builder()
        {
        }

        public Builder exportRootNode( NodePath exportRootNode )
        {
            this.exportRootNode = exportRootNode;
            return this;
        }

        public Builder batchSize( int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder exportHomePath( Path exportHomePath )
        {
            this.exportHomePath = exportHomePath;
            return this;
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder nodeExportWriter( ExportWriter exportWriter )
        {
            this.exportWriter = exportWriter;
            return this;
        }

        public Builder xmlNodeSerializer( XmlNodeSerializer xmlNodeSerializer )
        {
            this.xmlNodeSerializer = xmlNodeSerializer;
            return this;
        }

        public BatchedNodeExporter build()
        {
            return new BatchedNodeExporter( this );
        }
    }
}
