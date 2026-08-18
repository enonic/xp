package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.core.impl.export.xml.XmlNodeSerializer;
import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.NodeExportListener;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.util.BinaryReference;

import static java.util.Objects.requireNonNull;

public class NodeExporter
{
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * The most entries an unscrolled query may ask the index for at once. Unrelated to {@link Builder#batchSize(int)}, which sizes the
     * node reads.
     */
    private static final int LIST_BATCH_SIZE = 10_000;

    /**
     * The order a manually ordered parent gives its children: by the value an editor assigned, highest first, and by modification time
     * where a sibling was never assigned one. It mirrors {@link com.enonic.xp.index.ChildOrder#manualOrder()}, which the index applies
     * when the same listing is answered by a query.
     */
    private static final Comparator<Node> MANUAL_ORDER =
        Comparator.comparing( Node::getManualOrderValue, Comparator.nullsLast( Comparator.reverseOrder() ) )
            .thenComparing( Node::getTimestamp, Comparator.nullsLast( Comparator.reverseOrder() ) );

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
        this.xpVersion = requireNonNull( builder.xpVersion );
        this.batchSize = Math.max( 1, builder.batchSize );
        this.nodeExportListener = builder.nodeExportListener;
    }

    public NodeExportResult execute()
    {
        // every node an export reads is now resolved from storage, so the search index has nothing to contribute to it
        nodeService.refresh( RefreshMode.STORAGE );

        final Node rootNode = this.nodeService.getByPath( this.sourceNodePath );

        if ( rootNode != null )
        {
            doExportNodes( rootNode );
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
            nodeExportListener.nodeExported( 1 );
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

    private void doExportNodes( final Node rootNode )
    {
        // enumerated from storage, so an export covers the subtree the repository holds rather than the one the search index has caught
        // up with; the listing excludes the node the export was asked for. The count owed to the progress listener has to come from the
        // entries - the index alone counts without regard to what the caller is permitted to read.
        final List<NodeId> nodeIds = new ArrayList<>();
        nodeIds.add( rootNode.id() );

        String cursor = null;
        do
        {
            final ListNodesResult batch = nodeService.list( ListNodesParams.create()
                                                                .parentPath( rootNode.path() )
                                                                .recursive( true )
                                                                .batchSize( LIST_BATCH_SIZE )
                                                                .cursor( cursor )
                                                                .build() );
            for ( final NodeListEntry entry : batch.getEntries() )
            {
                nodeIds.add( entry.nodeId() );
            }
            cursor = batch.getCursor();
        }
        while ( cursor != null );

        if ( nodeExportListener != null )
        {
            nodeExportListener.nodeResolved( nodeIds.size() );
        }

        final Iterator<NodeId> iterator = nodeIds.iterator();

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

        // a manually ordered set is the handful of siblings an editor arranged by hand, so the children are read and ordered here
        // rather than through the search index, which leaves the export reading storage alone
        final ListNodesResult children = nodeService.list( ListNodesParams.create().parentPath( node.path() ).build() );

        final StringBuilder builder = new StringBuilder();

        nodeService.getByIds( children.getNodeIds() )
            .stream()
            .sorted( MANUAL_ORDER )
            .forEach( child -> builder.append( child.name().toString() ).append( LINE_SEPARATOR ) );

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
