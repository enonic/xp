package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEnumerationEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.util.BinaryReference;

import static java.util.Objects.requireNonNull;

public class NodeExporter
{
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final Comparator<OrderedChild> MANUAL_ORDER =
        Comparator.comparing( OrderedChild::manualOrderValue, Comparator.nullsLast( Comparator.reverseOrder() ) )
            .thenComparing( OrderedChild::timestamp, Comparator.nullsLast( Comparator.reverseOrder() ) );

    private static final Comparator<OrderedChild> ORDER_KEY_ORDER =
        Comparator.comparing( OrderedChild::orderKey, Comparator.nullsLast( Comparator.naturalOrder() ) )
            .thenComparing( OrderedChild::timestamp, Comparator.nullsLast( Comparator.reverseOrder() ) );

    private final NodePath sourceNodePath;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final Path targetDirectory;

    private final String xpVersion;

    private final NodeExportListener nodeExportListener;

    private final NodeExportResult.Builder result = NodeExportResult.create();

    private final Map<NodePath, List<OrderedChild>> childrenByParent = new HashMap<>();

    private final Set<NodePath> manualOrderParents = new HashSet<>();

    private final Set<NodePath> unorderedParents = new HashSet<>();

    private final Set<NodePath> orderKeyParents = new HashSet<>();

    private static final Logger LOG = LoggerFactory.getLogger( NodeExporter.class );

    private NodeExporter( final Builder builder )
    {
        this.sourceNodePath = builder.sourceNodePath;
        this.nodeService = builder.nodeService;
        this.exportWriter = builder.exportWriter;
        this.targetDirectory = builder.targetDirectory;
        this.xpVersion = requireNonNull( builder.xpVersion );
        this.nodeExportListener = builder.nodeExportListener;
    }

    public NodeExportResult execute()
    {
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
        boolean rootExported = false;
        int exported = 0;

        String cursor = null;
        do
        {
            final EnumerateNodesResult batch = nodeService.enumerate( EnumerateNodesParams.create()
                                                                          .parentPath( rootNode.path() )
                                                                          .cursor( cursor )
                                                                          .build() );
            if ( nodeExportListener != null )
            {
                nodeExportListener.resolved( 1 + exported + batch.getRemaining() );
            }

            if ( !rootExported )
            {
                exportNode( rootNode );
                rootExported = true;
            }

            for ( final NodeEnumerationEntry entry : batch.getEntries() )
            {
                exportEntry( entry );
                exported++;
            }

            cursor = batch.getCursor();
        }
        while ( cursor != null );

        writeNodeOrderLists();
    }

    private void exportEntry( final NodeEnumerationEntry entry )
    {
        final Node node;
        try
        {
            node = this.nodeService.getByIdAndVersionId( entry.nodeId(), entry.versionId() );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to export node with path [{}]", entry.nodePath(), e );
            result.addError( new ExportError( e.toString() ) );
            return;
        }

        exportNode( node );
    }

    private void exportNode( final Node node )
    {
        collectChildOrder( node );

        try
        {
            writeNode( node );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to export node with path [{}]", node.path(), e );
            result.addError( new ExportError( e.toString() ) );
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

    private void collectChildOrder( final Node node )
    {
        if ( node.getChildOrder() != null && node.getChildOrder().isManualOrder() )
        {
            manualOrderParents.add( node.path() );
        }
        else if ( node.getChildOrder() != null && node.getChildOrder().isOrderKeyOrder() )
        {
            orderKeyParents.add( node.path() );
        }
        else
        {
            unorderedParents.add( node.path() );
            childrenByParent.remove( node.path() );
        }

        final NodePath parentPath = node.parentPath();
        if ( !node.path().equals( sourceNodePath ) && !unorderedParents.contains( parentPath ) )
        {
            childrenByParent.computeIfAbsent( parentPath, key -> new ArrayList<>() )
                .add( new OrderedChild( node.name().toString(), node.getManualOrderValue(), node.getOrderKey(),
                                        node.getTimestamp() ) );
        }
    }

    private void writeNodeOrderLists()
    {
        writeNodeOrderLists( manualOrderParents, MANUAL_ORDER );
        writeNodeOrderLists( orderKeyParents, ORDER_KEY_ORDER );
    }

    private void writeNodeOrderLists( final Set<NodePath> parents, final Comparator<OrderedChild> order )
    {
        for ( final NodePath parentPath : parents )
        {
            final List<OrderedChild> children = childrenByParent.get( parentPath );

            if ( children == null )
            {
                continue;
            }

            try
            {
                children.sort( order );

                final StringBuilder builder = new StringBuilder();
                children.forEach( child -> builder.append( child.name() ).append( LINE_SEPARATOR ) );

                final Path nodeOrderListPath = resolveNodeDataFolder( parentPath ).resolve( NodeExportPathResolver.ORDER_EXPORT_NAME );

                exportWriter.writeElement( nodeOrderListPath, builder.toString() );
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to write child order of [{}]", parentPath, e );
                result.addError( new ExportError( e.toString() ) );
            }
        }
    }

    private record OrderedChild(String name, Long manualOrderValue, String orderKey, Instant timestamp)
    {
    }

    private void writeExportProperties()
    {
        final Path exportPropertiesPath = this.targetDirectory.resolve( NodeExportPathResolver.EXPORT_PROPERTIES_NAME );
        exportWriter.writeElement( exportPropertiesPath, "xp.version = " + xpVersion );
    }

    private Path resolveNodeDataFolder( final Node node )
    {
        return resolveNodeDataFolder( node.path() );
    }

    private Path resolveNodeDataFolder( final NodePath nodePath )
    {
        final Path fullNodePath = Path.of( nodePath.toString() );

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
