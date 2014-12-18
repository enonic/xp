package com.enonic.wem.export.internal;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.BinaryAttachment;
import com.enonic.wem.api.node.BinaryAttachments;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.InsertManualStrategy;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.export.internal.builder.CreateNodeParamsFactory;
import com.enonic.wem.export.internal.reader.ExportReader;
import com.enonic.wem.export.internal.reader.NodeImportPathResolver;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.XmlAttachedBinaries;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

public class NodeImportCommand
{
    private final NodePath importRoot;

    private final NodeService nodeService;

    private final ExportReader exportReader;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final Path exportRootPath;

    private final boolean dryRun;

    private final NodeImportResult.Builder result = NodeImportResult.create();

    private static final Long IMPORT_NODE_ORDER_START_VALUE = 0l;

    private static final Long IMPORT_NODE_ORDER_SPACE = (long) Integer.MAX_VALUE;


    private NodeImportCommand( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.exportReader = builder.exportReader;
        this.exportRootPath = NodeExportPathResolver.resolveExportTargetPath( builder.exportHome, builder.exportName );
        this.xmlNodeSerializer = builder.xmlNodeSerializer;
        this.importRoot = builder.importRoot;
        this.dryRun = builder.dryRun;
    }

    public NodeImportResult execute()
    {
        verifyImportRoot();

        importFromDirectoryLayout( this.exportRootPath );

        return this.result.build();
    }

    private void importFromDirectoryLayout( final Path parentPath )
    {
        final Stream<Path> children = getChildPaths( parentPath );

        children.filter( this::isNodeFolder ).
            forEach( ( child ) -> processNodeBasePath( child, ProcessNodeSettings.create() ) );
    }

    private boolean isNodeFolder( final Path path )
    {
        return !( path.endsWith( Paths.get( NodeExportPathResolver.SYSTEM_FOLDER_NAME ) ) );
    }

    private void importWithManualOrder( final Path nodeBasePath )
    {
        final List<String> childNames = processManualOrderFile( nodeBasePath );

        long currentManualOrderValue = IMPORT_NODE_ORDER_START_VALUE;

        for ( final String childName : childNames )
        {
            final Path childNodePath = NodeImportPathResolver.resolveChildNodePath( nodeBasePath, childName );

            final ProcessNodeSettings.Builder processNodeSettings = ProcessNodeSettings.create().
                insertManualStrategy( InsertManualStrategy.MANUAL ).
                manualOrderValue( currentManualOrderValue );

            if ( childNodePath != null )
            {
                processNodeBasePath( childNodePath, processNodeSettings );
            }

            currentManualOrderValue -= IMPORT_NODE_ORDER_SPACE;
        }
    }

    private void processNodeBasePath( final Path nodeBasePath, final ProcessNodeSettings.Builder processNodeSettings )
    {
        final Node node = processNodeXmlFile( nodeBasePath, processNodeSettings );

        if ( !node.getChildOrder().isManualOrder() )
        {
            importFromDirectoryLayout( nodeBasePath );
        }
        else
        {
            importWithManualOrder( nodeBasePath );
        }
    }

    private Node processNodeXmlFile( final Path nodeBasePath, final ProcessNodeSettings.Builder processNodeSettings )
    {
        final XmlNode xmlNode = getXmlNodeFromPath( nodeBasePath );

        final NodePath importNodePath =
            NodeImportPathResolver.resolveImportedNodePath( nodeBasePath, this.exportRootPath, this.importRoot );

        final BinaryAttachments binaryAttachments = resolveBinaryAttachments( nodeBasePath, xmlNode );

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create().
            processNodeSettings( processNodeSettings.build() ).
            xmlNode( xmlNode ).
            importPath( importNodePath ).
            binaryAttachments( binaryAttachments ).
            build().
            execute();

        final Node createdNode = this.nodeService.create( createNodeParams );

        result.add( createdNode.path() );

        return createdNode;
    }

    private BinaryAttachments resolveBinaryAttachments( final Path nodeBasePath, final XmlNode xmlNode )
    {
        final XmlAttachedBinaries attachedBinaries = xmlNode.getAttachedBinaries();

        if ( attachedBinaries == null )
        {
            return BinaryAttachments.empty();
        }

        final BinaryAttachments.Builder builder = BinaryAttachments.create();

        for ( final XmlAttachedBinaries.AttachedBinary attachedBinary : attachedBinaries.getAttachedBinary() )
        {
            final String binaryReference = attachedBinary.getBinaryReference();

            final Path binaryFilePath = NodeImportPathResolver.resolveBinaryFilePath( nodeBasePath, binaryReference );

            final ByteSource binarySource = this.exportReader.getSource( binaryFilePath );

            if ( binarySource == null )
            {
                throw new ImportNodeException( "Missing binary file, expected file: " + binaryFilePath );
            }

            builder.add( new BinaryAttachment( BinaryReference.from( binaryReference ), binarySource ) );
        }

        return builder.build();
    }

    private List<String> processManualOrderFile( final Path nodeBasePath )
    {
        final Path orderFilePath = NodeImportPathResolver.resolveOrderFilePath( nodeBasePath );

        if ( !this.exportReader.getFile( orderFilePath ).exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected file " + orderFilePath );
        }

        return this.exportReader.readLines( orderFilePath );
    }

    private XmlNode getXmlNodeFromPath( final Path nodeBasePath )
    {
        final Path nodeXmlFilePath = NodeImportPathResolver.resolveNodeXmlFilePath( nodeBasePath );

        final File nodeXmlFile = this.exportReader.getFile( nodeXmlFilePath );

        if ( !nodeXmlFile.exists() )
        {
            throw new ImportNodeException( "Node file not found: " + nodeXmlFilePath );
        }

        final String serializedNode = this.exportReader.readItem( nodeXmlFilePath );
        return this.xmlNodeSerializer.parse( serializedNode );
    }

    private void verifyImportRoot()
    {
        if ( NodePath.ROOT.equals( this.importRoot ) )
        {
            return;
        }

        final Node importRoot = nodeService.getByPath( this.importRoot );

        if ( importRoot == null )
        {
            throw new ImportNodeException( "Import root '" + this.importRoot + "' not found" );
        }
    }


    private Stream<Path> getChildPaths( final Path path )
    {
        return this.exportReader.getChildrenPaths( path );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath importRoot;

        private NodeService nodeService;

        private ExportReader exportReader;

        private XmlNodeSerializer xmlNodeSerializer;

        private Path exportHome;

        private String exportName;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder importRoot( NodePath nodePath )
        {
            this.importRoot = nodePath;
            return this;
        }

        public Builder exportHome( Path basePath )
        {
            this.exportHome = basePath;
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

        public Builder exportReader( ExportReader exportReader )
        {
            this.exportReader = exportReader;
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

        public NodeImportCommand build()
        {
            return new NodeImportCommand( this );
        }
    }

}

