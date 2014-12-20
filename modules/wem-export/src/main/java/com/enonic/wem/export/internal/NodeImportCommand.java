package com.enonic.wem.export.internal;

import java.util.List;
import java.util.stream.Stream;

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
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.export.internal.builder.CreateNodeParamsFactory;
import com.enonic.wem.export.internal.reader.ExportReader;
import com.enonic.wem.export.internal.xml.XmlAttachedBinaries;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

public class NodeImportCommand
{
    private final NodePath importRoot;

    private final NodeService nodeService;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final VirtualFile exportRoot;

    private final ExportReader exportReader = new ExportReader();

    private final boolean dryRun;

    private final NodeImportResult.Builder result = NodeImportResult.create();

    private static final Long IMPORT_NODE_ORDER_START_VALUE = 0l;

    private static final Long IMPORT_NODE_ORDER_SPACE = (long) Integer.MAX_VALUE;

    private NodeImportCommand( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.exportRoot = builder.exportRoot;
        this.xmlNodeSerializer = builder.xmlNodeSerializer;
        this.importRoot = builder.importRoot;
        this.dryRun = builder.dryRun;
    }

    public NodeImportResult execute()
    {
        verifyImportRoot();

        importFromDirectoryLayout( this.exportRoot );

        return this.result.build();
    }

    private void importFromDirectoryLayout( final VirtualFile parentFolder )
    {
        final Stream<VirtualFile> children = this.exportReader.getChildren( parentFolder );

        children.forEach( ( child ) -> {
            processNodeFolder( child, ProcessNodeSettings.create() );
        } );
    }

    private void importFromManualOrder( final VirtualFile nodeFolder )
    {
        final List<String> childNames;

        try
        {
            childNames = processBinarySource( nodeFolder );
        }
        catch ( Exception e )
        {
            result.addError( "Not able to import nodes by manual order, using default ordering", e );
            importFromDirectoryLayout( nodeFolder );
            return;
        }

        long currentManualOrderValue = IMPORT_NODE_ORDER_START_VALUE;

        for ( final String childName : childNames )
        {
            final VirtualFile child = nodeFolder.resolve( childName );

            final ProcessNodeSettings.Builder processNodeSettings = ProcessNodeSettings.create().
                insertManualStrategy( InsertManualStrategy.MANUAL ).
                manualOrderValue( currentManualOrderValue );

            if ( child != null )
            {
                processNodeFolder( child, processNodeSettings );
            }

            currentManualOrderValue -= IMPORT_NODE_ORDER_SPACE;
        }
    }

    private void processNodeFolder( final VirtualFile nodeFolder, final ProcessNodeSettings.Builder processNodeSettings )
    {
        try
        {
            final Node node = processNodeSource( nodeFolder, processNodeSettings );

            if ( !node.getChildOrder().isManualOrder() )
            {
                importFromDirectoryLayout( nodeFolder );
            }
            else
            {
                importFromManualOrder( nodeFolder );
            }
        }
        catch ( Exception e )
        {
            result.addError( e );
        }
    }

    private Node processNodeSource( final VirtualFile nodeFolder, final ProcessNodeSettings.Builder processNodeSettings )
    {
        final VirtualFile nodeSource = this.exportReader.getNodeSource( nodeFolder );

        final XmlNode xmlNode = this.xmlNodeSerializer.parse( nodeSource.getByteSource() );

        final NodePath importNodePath = NodeImportPathResolver.resolveNodeImportPath( nodeFolder, this.exportRoot, this.importRoot );

        final BinaryAttachments binaryAttachments = processBinaryAttachments( nodeFolder, xmlNode );

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

    private List<String> processBinarySource( final VirtualFile nodeFolder )
        throws Exception
    {
        final VirtualFile orderFile = this.exportReader.getOrderSource( nodeFolder );
        return orderFile.getCharSource().readLines();
    }

    private BinaryAttachments processBinaryAttachments( final VirtualFile nodeFile, final XmlNode xmlNode )
    {
        final XmlAttachedBinaries attachedBinaries = xmlNode.getAttachedBinaries();

        if ( attachedBinaries == null )
        {
            return BinaryAttachments.empty();
        }

        final BinaryAttachments.Builder builder = BinaryAttachments.create();

        for ( final XmlAttachedBinaries.AttachedBinary attachedBinary : attachedBinaries.getAttachedBinary() )
        {
            addBinary( nodeFile, builder, attachedBinary );
        }

        return builder.build();
    }

    private void addBinary( final VirtualFile nodeFile, final BinaryAttachments.Builder builder,
                            final XmlAttachedBinaries.AttachedBinary attachedBinary )
    {
        final String binaryReferenceString = attachedBinary.getBinaryReference();

        try
        {
            final VirtualFile binary = exportReader.getBinarySource( nodeFile, binaryReferenceString );
            final BinaryReference binaryReference = BinaryReference.from( binaryReferenceString );
            builder.add( new BinaryAttachment( binaryReference, binary.getByteSource() ) );

            result.addBinary( binary.getUrl(), binaryReference );
        }
        catch ( Exception e )
        {
            result.addError( "Error processing binary, skip", e );
        }
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath importRoot;

        private NodeService nodeService;

        private XmlNodeSerializer xmlNodeSerializer;

        private VirtualFile exportRoot;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder importRoot( NodePath nodePath )
        {
            this.importRoot = nodePath;
            return this;
        }

        public Builder exportRoot( VirtualFile exportRoot )
        {
            this.exportRoot = exportRoot;
            return this;
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
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

