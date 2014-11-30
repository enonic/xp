package com.enonic.wem.export.internal;

import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.writer.ExportItemPath;
import com.enonic.wem.export.internal.writer.ExportWriter;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.mapper.XmlNodeMapper;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

public class BatchedNodeExporter
{
    private final static int DEFAULT_BATCH_SIZE = 100;

    public static final String NODE_POSTFIX = ".xml";

    private final NodePath nodePath;

    private final int batchSize;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final ExportItemPath rootPath;

    private BatchedNodeExporter( Builder builder )
    {
        nodePath = builder.nodePath;
        batchSize = builder.batchSize;
        nodeService = builder.nodeService;
        exportWriter = builder.exportWriter;
        xmlNodeSerializer = builder.xmlNodeSerializer;
        this.rootPath = NodeExportPathResolver.resolveRoot( builder.basePath, "node" );
    }

    public NodeExportResult export()
    {
        this.exportWriter.createDirectory( rootPath );

        final NodeExportResult.Builder resultBuilder = NodeExportResult.create();

        doProcessChildren( this.nodePath, resultBuilder );

        return resultBuilder.build();
    }

    private void doProcessChildren( final NodePath nodePath, final NodeExportResult.Builder resultBuilder )
    {
        final FindNodesByParentResult countResult = nodeService.findByParent( FindNodesByParentParams.create().
            countOnly( true ).
            parentPath( nodePath ).
            build() );

        final long totalHits = countResult.getTotalHits();

        final double batches = getBatchSize( totalHits );

        int currentFrom = 0;

        for ( int i = 1; i <= batches; i++ )
        {
            final FindNodesByParentResult currentLevelChildren = nodeService.findByParent( FindNodesByParentParams.create().
                parentPath( nodePath ).
                from( currentFrom ).
                size( this.batchSize ).
                build() );

            for ( final Node child : currentLevelChildren.getNodes() )
            {
                exportNode( child );
                resultBuilder.add( child.path() );
                doProcessChildren( child.path(), resultBuilder );
            }

            currentFrom += this.batchSize;
        }
    }

    private double getBatchSize( final double totalHits )
    {
        return Math.ceil( totalHits / this.batchSize );
    }

    private void exportNode( final Node node )
    {
        final XmlNode xmlNode = XmlNodeMapper.toXml( node );

        final String serializedNode = this.xmlNodeSerializer.serialize( xmlNode );

        final ExportItemPath systemFolder = createNodeSystemFolder( node );

        exportWriter.writeElement( NodeExportPathResolver.resolveNodeXmlPath( systemFolder, node ), serializedNode );
    }

    private ExportItemPath createNodeSystemFolder( final Node node )
    {
        final ExportItemPath nodeBasePath = NodeExportPathResolver.resolveNodeBasePath( this.rootPath, node );

        exportWriter.createDirectory( nodeBasePath );

        final ExportItemPath systemFolder = NodeExportPathResolver.resolveSystemFolder( nodeBasePath );

        exportWriter.createDirectory( systemFolder );
        return systemFolder;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodePath nodePath;

        private int batchSize = DEFAULT_BATCH_SIZE;

        private NodeService nodeService;

        private ExportWriter exportWriter;

        private XmlNodeSerializer xmlNodeSerializer;

        private ExportItemPath basePath;

        private Builder()
        {
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder batchSize( int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder basePath( ExportItemPath basePath )
        {
            this.basePath = basePath;
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
