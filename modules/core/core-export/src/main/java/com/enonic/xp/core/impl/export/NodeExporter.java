package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Objects;

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
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.util.BinaryReference;

public class NodeExporter
{
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final int BATCH_SIZE = 100;

    private final NodePath sourceNodePath;

    private final NodeService nodeService;

    private final ExportWriter exportWriter;

    private final Path targetDirectory;

    private final String xpVersion;

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

            try
            {
                exportNode( rootNode );
            }
            catch ( Exception e )
            {
                LOG.error( String.format( "Failed to export node with path [%s]", rootNode.path() ), e );
                result.addError( new ExportError( e.toString() ) );
            }
        }
        else
        {
            addRootNodeNotFoundError();
        }

        writeExportProperties();

        return result.build();
    }


    private void exportNode( final Node node )
    {
        writeNode( node );

        if ( nodeExportListener != null )
        {
            nodeExportListener.nodeExported( 1L );
        }

        result.addNodePath( node.path() );
        doExportChildNodes( node.path() );
    }

    private void writeNode( final Node node )
    {
        doWriteNode( node, resolveNodeDataFolder( node ) );
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

    private void doExportChildNodes( final NodePath parentPath )
    {
        final Node parentNode = nodeService.getByPath( parentPath );

        doExport( parentPath );

        // For manual ordering, we need to write the order list
        // We need the names in the correct order, so we load nodes in batches
        if ( parentNode != null && parentNode.getChildOrder() != null && parentNode.getChildOrder().isManualOrder() )
        {
            final FindNodesByParentResult totalResult =
                nodeService.findByParent( FindNodesByParentParams.create().parentPath( parentPath ).size( 0 ).build() );

            final long totalHits = totalResult.getTotalHits();

            final StringBuilder orderBuilder = new StringBuilder();

            int from = 0;
            while ( from < totalHits )
            {
                final FindNodesByParentResult batch = nodeService.findByParent(
                    FindNodesByParentParams.create().parentPath( parentPath ).from( from ).size( BATCH_SIZE ).build() );

                final Nodes childNodes = this.nodeService.getByIds( batch.getNodeIds() );

                for ( final Node node : childNodes )
                {
                    orderBuilder.append( node.name().toString() ).append( LINE_SEPARATOR );
                }

                from += BATCH_SIZE;
            }

            final Path nodeOrderListPath = resolveNodeDataFolder( parentNode ).resolve( NodeExportPathResolver.ORDER_EXPORT_NAME );
            exportWriter.writeElement( nodeOrderListPath, orderBuilder.toString() );
        }
    }

    private FindNodesByParentResult doExport( final NodePath nodePath )
    {
        final FindNodesByParentResult totalResult =
            nodeService.findByParent( FindNodesByParentParams.create().parentPath( nodePath ).size( 0 ).build() );

        final long totalHits = totalResult.getTotalHits();

        int from = 0;
        while ( from < totalHits )
        {
            final FindNodesByParentResult batch = nodeService.findByParent(
                FindNodesByParentParams.create().parentPath( nodePath ).from( from ).size( BATCH_SIZE ).build() );

            final Nodes childNodes = this.nodeService.getByIds( batch.getNodeIds() );

            for ( final Node child : childNodes )
            {
                try
                {
                    exportNode( child );
                }
                catch ( Exception e )
                {
                    LOG.error( String.format( "Failed to export node with path [%s]", child.path() ), e );
                    result.addError( new ExportError( e.toString() ) );
                }
            }

            from += BATCH_SIZE;
        }

        return totalResult;
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
