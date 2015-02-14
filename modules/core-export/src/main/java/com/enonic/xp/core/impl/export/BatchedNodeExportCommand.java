package com.enonic.xp.core.impl.export;

import java.nio.file.Path;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.context.ContextAccessor;
import com.enonic.xp.core.export.ExportError;
import com.enonic.xp.core.export.NodeExportResult;
import com.enonic.xp.core.node.AttachedBinary;
import com.enonic.xp.core.node.FindNodesByParentParams;
import com.enonic.xp.core.node.FindNodesByParentResult;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.NodeService;
import com.enonic.xp.core.node.Nodes;
import com.enonic.xp.core.util.BinaryReference;
import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.core.impl.export.xml.XmlNode;
import com.enonic.xp.core.impl.export.xml.mapper.XmlNodeMapper;
import com.enonic.xp.core.impl.export.xml.serializer.XmlNodeSerializer;

public class BatchedNodeExportCommand
{
    private final static int DEFAULT_BATCH_SIZE = 100;

    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private final NodePath sourceNodePath;

    private final int batchSize;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final Path targetDirectory;

    private final boolean dryRun;

    private final boolean exportNodeIds;

    private final NodeExportResult.Builder result = NodeExportResult.create();

    private BatchedNodeExportCommand( final Builder builder )
    {
        this.sourceNodePath = builder.sourceNodePath;
        this.batchSize = builder.batchSize;
        this.nodeService = builder.nodeService;
        this.exportWriter = builder.exportWriter;
        this.xmlNodeSerializer = builder.xmlNodeSerializer;
        this.targetDirectory = builder.targetDirectory;
        this.dryRun = builder.dryRun;
        this.exportNodeIds = builder.exportNodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeExportResult execute()
    {
        this.result.dryRun( this.dryRun );

        if ( this.sourceNodePath.isRoot() )
        {
            doExportChildNodes( this.sourceNodePath );
        }
        else
        {
            final Node rootNode = this.nodeService.getByPath( this.sourceNodePath );

            if ( rootNode != null )
            {
                exportNode( rootNode );
            }
            else
            {
                addRootNodeNotFoundError();
            }
        }

        return result.build();
    }

    private void doExportChildNodes( final NodePath parentPath )
    {
        final Node parentNode = nodeService.getByPath( parentPath );

        final double batches = getNumberOfBatches( parentPath );

        int currentFrom = 0;

        final Nodes.Builder allCurrentLevelChildren = Nodes.create();

        for ( int i = 1; i <= batches; i++ )
        {
            final FindNodesByParentResult childrenBatch = exportBatch( parentPath, currentFrom );

            allCurrentLevelChildren.addAll( childrenBatch.getNodes() );
            currentFrom += this.batchSize;
        }

        writeNodeOrderList( parentNode, allCurrentLevelChildren.build() );
    }

    private FindNodesByParentResult exportBatch( final NodePath nodePath, final int currentFrom )
    {
        final FindNodesByParentResult childrenBatch = nodeService.findByParent( FindNodesByParentParams.create().
            parentPath( nodePath ).
            from( currentFrom ).
            size( this.batchSize ).
            build() );

        for ( final Node child : childrenBatch.getNodes() )
        {
            try
            {
                exportNode( child );
            }
            catch ( Exception e )
            {
                result.addError( new ExportError( e.toString() ) );
            }
        }
        return childrenBatch;
    }

    private void exportNode( final Node node )
    {
        writeNode( node );
        result.addNodePath( node.path() );
        doExportChildNodes( node.path() );
    }

    private void writeNode( final Node node )
    {
        final NodePath newParentPath = resolveNewParentPath( node );

        final Node relativeNode = Node.newNode( node ).parentPath( newParentPath ).build();

        final XmlNode xmlNode = XmlNodeMapper.toXml( relativeNode, this.exportNodeIds );

        final String serializedNode = this.xmlNodeSerializer.serialize( xmlNode );

        final Path nodeDataFolder = resolveNodeDataFolder( node );

        if ( !dryRun )
        {
            final Path nodeXmlPath = NodeExportPathResolver.resolveNodeXmlPath( nodeDataFolder );
            exportWriter.writeElement( nodeXmlPath, serializedNode );
        }

        exportNodeBinaries( relativeNode, nodeDataFolder );
    }

    private NodePath resolveNewParentPath( final Node node )
    {
        final NodePath newParentPath;

        if ( node.path().equals( this.sourceNodePath ) )
        {
            newParentPath = NodePath.ROOT;
        }
        else
        {
            newParentPath = node.parentPath().removeFromBeginning( this.sourceNodePath );
        }
        return newParentPath;
    }

    private void exportNodeBinaries( final Node relativeNode, final Path nodeDataFolder )
    {
        for ( final AttachedBinary attachedBinary : relativeNode.getAttachedBinaries() )
        {
            final BinaryReference reference = attachedBinary.getBinaryReference();
            final ByteSource byteSource = this.nodeService.getBinary( relativeNode.id(), reference );

            if ( !dryRun )
            {
                this.exportWriter.writeSource( NodeExportPathResolver.resolveBinaryPath( nodeDataFolder, reference ), byteSource );
            }

            result.addBinary( relativeNode.path(), reference );
        }
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

        final Path nodeOrderListPath = NodeExportPathResolver.resolveOrderListPath( resolveNodeDataFolder( parent ) );

        if ( !dryRun )
        {
            exportWriter.writeElement( nodeOrderListPath, builder.toString() );
        }
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

    private Path resolveNodeDataFolder( final Node node )
    {
        final Path nodeBasePath = NodeExportPathResolver.resolveNodeBasePath( this.targetDirectory, node.path(), sourceNodePath );
        return NodeExportPathResolver.resolveNodeDataFolder( nodeBasePath );
    }

    private void addRootNodeNotFoundError()
    {
        result.addError( new ExportError(
            "Node with path '" + this.sourceNodePath + "' not found in branch '" + ContextAccessor.current().getBranch().getName() +
                "', nothing to export" ) );
    }

    public static final class Builder
    {
        private NodePath sourceNodePath;

        private int batchSize = DEFAULT_BATCH_SIZE;

        private NodeService nodeService;

        private ExportWriter exportWriter;

        private XmlNodeSerializer xmlNodeSerializer;

        private Path targetDirectory;

        private boolean dryRun = false;

        private boolean exportNodeIds = true;

        private Builder()
        {
        }

        public Builder sourceNodePath( NodePath exportRootNode )
        {
            this.sourceNodePath = exportRootNode;
            return this;
        }

        public Builder batchSize( int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder targetDirectory( Path targetDirectory )
        {
            this.targetDirectory = targetDirectory;
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

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder exportNodeIds( final boolean exportNodeIds )
        {
            this.exportNodeIds = exportNodeIds;
            return this;
        }

        public BatchedNodeExportCommand build()
        {
            return new BatchedNodeExportCommand( this );
        }
    }
}
