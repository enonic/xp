package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.core.impl.export.xml.XmlNodeSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.util.BinaryReference;

public class NodeExporter
{
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final NodePath sourceNodePath;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final Path targetDirectory;

    private final String xpVersion;

    private final int batchSize;

    private final NodeExportListener nodeExportListener;

    private final NodeExportResult.Builder result = NodeExportResult.create();

    private static final Logger LOG = LoggerFactory.getLogger( NodeExporter.class );

    private NodeExporter( final Builder builder )
    {
        this.sourceNodePath = builder.sourceNodePath;
        this.nodeService = builder.nodeService;
        this.exportWriter = builder.exportWriter;
        this.targetDirectory = builder.targetDirectory;
        this.xpVersion = Objects.requireNonNull( builder.xpVersion );
        this.batchSize = builder.batchSize;
        this.nodeExportListener = builder.nodeExportListener;
    }

    public NodeExportResult execute()
    {
        nodeService.refresh( RefreshMode.ALL );

        final Node rootNode = this.nodeService.getByPath( this.sourceNodePath );

        if ( rootNode != null )
        {
            if ( nodeExportListener != null )
            {
                final long childNodeCount = getRecursiveNodeCountByParentPath( sourceNodePath );
                nodeExportListener.nodeResolved( childNodeCount + 1 );
            }

//            try
//            {
//                writeNode( rootNode );
//                writeNodeOrderList( rootNode );
            doExportNodes( rootNode.path() );
//            }
//            catch ( Exception e )
//            {
//                LOG.error( String.format( "Failed to export node with path [%s]", rootNode.path() ), e );
//                result.addError( new ExportError( e.toString() ) );
//            }
        }
        else
        {
            addRootNodeNotFoundError();
        }

        writeExportProperties();

        return result.build();
    }


    private void writeNode( final Node node )
    {
        if ( nodeExportListener != null )
        {
            nodeExportListener.nodeExported( 1L );
        }

        doWriteNode( node, resolveNodeDataFolder( node ) );

        result.addNodePath( node.path() );

    }

    private void doWriteNode( final Node node, final Path baseFolder )
    {
        final NodePath newParentPath = new NodePath( "/" + node.toString().substring( this.sourceNodePath.toString().length() ) );

        final Node relativeNode = Node.create( node ).parentPath( newParentPath ).build();

        final String serializedNode = new XmlNodeSerializer().node( relativeNode ).serialize();

        final Path nodeXmlPath = baseFolder.resolve( NodeExportPathResolver.NODE_XML_EXPORT_NAME );
        exportWriter.writeElement( nodeXmlPath, serializedNode );

        exportNodeBinaries( relativeNode, baseFolder );
    }

    private void doExportNodes( final NodePath parentPath )
    {
        final QueryExpr nodesQuery = parentPath.isRoot()
            ? QueryExpr.from( DslExpr.from( new PropertyTree() ) )
            : QueryExpr.from(
                LogicalExpr.or( CompareExpr.eq( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string( parentPath.toString() ) ),
                                CompareExpr.like( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string( parentPath + "/*" ) ) ) );

        final FindNodesByQueryResult nodeIds = nodeService.findByQuery( NodeQuery.create()
                                                                            .query( nodesQuery )
                                                                            .addOrderBy( FieldOrderExpr.create( NodeIndexPath.PATH,
                                                                                                                OrderExpr.Direction.ASC ) )
                                                                            .size( -1 )
                                                                            .build() );

        final Iterator<NodeId> iterator = nodeIds.getNodeIds().iterator();

        while ( iterator.hasNext() )
        {
            final NodeIds.Builder batch = NodeIds.create();

            for ( int i = 0; i < batchSize && iterator.hasNext(); i++ )
            {
                batch.add( iterator.next() );
            }

            final NodeIds batchNodeIds = batch.build();

            if ( batchNodeIds.isEmpty() )
            {
                return;
            }

            final Nodes exportNodes = this.nodeService.getByIds( batchNodeIds );

            for ( final Node child : exportNodes )
            {
                try
                {
                    writeNode( child );
                    writeNodeOrderList( child );
                }
                catch ( Exception e )
                {
                    LOG.error( "Failed to export node with path [{}]", child.path(), e );
                    result.addError( new ExportError( e.toString() ) );
                }
            }
        }
    }

    private void exportNodeBinaries( final Node relativeNode, final Path nodeDataFolder )
    {
        for ( final AttachedBinary attachedBinary : relativeNode.getAttachedBinaries() )
        {
            final BinaryReference reference = attachedBinary.getBinaryReference();
            final ByteSource byteSource = this.nodeService.getBinary( relativeNode.id(), relativeNode.getNodeVersionId(), reference );

            this.exportWriter.writeSource( nodeDataFolder.resolve( NodeExportPathResolver.BINARY_FOLDER ).resolve( reference.toString() ),
                                           byteSource );

            result.addBinary( relativeNode.path(), reference );
        }
    }

    private void writeNodeOrderList( final Node node )
    {
        if ( node == null || node.getChildOrder() == null || !node.getChildOrder().isManualOrder() )
        {
            return;
        }

        final StringBuilder builder = new StringBuilder();

        final FindNodesByQueryResult findResult = nodeService.findByQuery( NodeQuery.create()
                                                                               .parent( node.path() )
                                                                               .setOrderExpressions(
                                                                                   node.getChildOrder().getOrderExpressions() )
                                                                               .withPath( true )
                                                                               .size( -1 )
                                                                               .build() );

        for ( final NodeHit child : findResult.getNodeHits() )
        {
            builder.append( child.getNodePath().getName().toString() ).append( LINE_SEPARATOR );
        }

        if ( builder.isEmpty() )
        {
            return;
        }

        final Path nodeOrderListPath = resolveNodeDataFolder( node ).resolve( NodeExportPathResolver.ORDER_EXPORT_NAME );

        exportWriter.writeElement( nodeOrderListPath, builder.toString() );
    }

    private void writeExportProperties()
    {
        final Path exportPropertiesPath = this.targetDirectory.resolve( NodeExportPathResolver.EXPORT_PROPERTIES_NAME );
        exportWriter.writeElement( exportPropertiesPath, "xp.version = " + xpVersion );
    }

    private long getRecursiveNodeCountByParentPath( final NodePath nodePath )
    {
        return nodeService.findByParent(
            FindNodesByParentParams.create().countOnly( true ).parentPath( nodePath ).recursive( true ).build() ).getTotalHits();
    }

    private Path resolveNodeDataFolder( final Node node )
    {
        final Path fullNodePath = Path.of( node.path().toString() );

        final Path exportBasePath;

        if ( sourceNodePath.isRoot() )
        {
            exportBasePath = Path.of( NodePath.ROOT.toString() );
        }
        else
        {
            exportBasePath = Path.of( sourceNodePath.getParentPath().toString() );
        }

        final Path relativePath = exportBasePath.relativize( fullNodePath );

        return this.targetDirectory.resolve( relativePath ).resolve( NodeExportPathResolver.SYSTEM_FOLDER_NAME );
    }

    private void addRootNodeNotFoundError()
    {
        result.addError( new ExportError(
            "Node with path '" + this.sourceNodePath + "' not found in branch '" + ContextAccessor.current().getBranch() +
                "', nothing to export" ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath sourceNodePath;

        private NodeService nodeService;

        private ExportWriter exportWriter;

        private Path targetDirectory;

        private String xpVersion;

        private int batchSize;

        private NodeExportListener nodeExportListener;

        private Builder()
        {
        }

        public Builder sourceNodePath( NodePath exportRootNode )
        {
            this.sourceNodePath = exportRootNode;
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

        public Builder xpVersion( final String xpVersion )
        {
            this.xpVersion = xpVersion;
            return this;
        }

        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder nodeExportListener( final NodeExportListener nodeExportListener )
        {
            this.nodeExportListener = nodeExportListener;
            return this;
        }

        public NodeExporter build()
        {
            return new NodeExporter( this );
        }
    }
}
