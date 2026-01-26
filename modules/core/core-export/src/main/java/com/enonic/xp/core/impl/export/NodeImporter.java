package com.enonic.xp.core.impl.export;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.io.CharSource;

import com.enonic.xp.core.impl.export.reader.ExportReader;
import com.enonic.xp.core.impl.export.validator.ContentImportValidator;
import com.enonic.xp.core.impl.export.validator.ImportValidator;
import com.enonic.xp.core.impl.export.xml.XmlNodeParser;
import com.enonic.xp.core.impl.export.xml.XsltTransformer;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.export.ImportNodeException;
import com.enonic.xp.export.NodeImportListener;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;
import com.enonic.xp.xml.XmlException;

import static com.enonic.xp.core.impl.export.ExportConstants.BINARY_FOLDER;
import static com.enonic.xp.core.impl.export.ExportConstants.NODE_XML_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.ExportConstants.ORDER_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.ExportConstants.SYSTEM_FOLDER_NAME;

public final class NodeImporter
{
    private static final Long IMPORT_NODE_ORDER_START_VALUE = 0L;

    private static final Long IMPORT_NODE_ORDER_SPACE = (long) Integer.MAX_VALUE;

    private final NodePath importRoot;

    private final NodeService nodeService;

    private final VirtualFile exportRoot;

    private final ExportReader exportReader = new ExportReader();

    private final NodeImportResult.Builder result = NodeImportResult.create();

    private final boolean importNodeIds;

    private final boolean importPermissions;

    private final Set<ImportValidator> importValidators = Set.of( new ContentImportValidator() );

    private final XsltTransformer transformer;

    private final NodeImportListener nodeImportListener;

    private NodeImporter( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.exportRoot = builder.exportRoot;
        this.importRoot = builder.importRoot;
        this.importNodeIds = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
        this.transformer = builder.xslt != null ? XsltTransformer.create( builder.xslt.getByteSource(), builder.xsltParams ) : null;
        this.nodeImportListener = builder.nodeImportListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeImportResult execute()
    {
        if ( nodeImportListener != null )
        {
            nodeImportListener.nodeResolved( exportReader.getNodeFileCount( exportRoot ) );
        }

        if ( !isNodeFolder( this.exportRoot ) )
        {
            importFromDirectoryLayout( this.exportRoot );
        }
        else
        {
            // Export root contains a node definition - should be created as the node
            // given as importRoot
            verifyImportRoot();
            processNodeFolder( this.exportRoot, ProcessNodeSettings.create().build() );
        }

        nodeService.refresh( RefreshMode.ALL );

        return this.result.build();
    }

    private void importFromDirectoryLayout( final VirtualFile parentFolder )
    {
        final Stream<VirtualFile> children = this.exportReader.getChildren( parentFolder );

        children.forEach( ( child ) -> processNodeFolder( child, ProcessNodeSettings.create().build() ) );
    }

    private void importFromManualOrder( final VirtualFile nodeFolder )
    {
        final List<String> childNames;

        try
        {
            final List<String> relativeChildNames = processBinarySource( nodeFolder );
            childNames = getChildrenAbsolutePaths( nodeFolder, relativeChildNames );
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
            final VirtualFile child = nodeFolder.resolve( VirtualFilePaths.from( childName, "/" ) );

            final ProcessNodeSettings processNodeSettings = ProcessNodeSettings.create()
                .insertManualStrategy( InsertManualStrategy.MANUAL )
                .manualOrderValue( currentManualOrderValue )
                .build();

            if ( child != null && child.exists() )
            {
                processNodeFolder( child, processNodeSettings );
            }

            currentManualOrderValue -= IMPORT_NODE_ORDER_SPACE;
        }
    }

    private List<String> getChildrenAbsolutePaths( final VirtualFile parent, final List<String> childNames )
    {
        final List<String> children = new ArrayList<>();

        for ( final String childName : childNames )
        {
            final VirtualFilePath join = parent.getPath().join( childName );
            children.add( join.getPath() );
        }

        return children;
    }

    private void processNodeFolder( final VirtualFile nodeFolder, final ProcessNodeSettings processNodeSettings )
    {
        Node node = null;
        try
        {
            node = processNodeSource( nodeFolder, processNodeSettings );
        }
        catch ( Exception e )
        {
            result.addError( "Could not import node in folder [" + nodeFolder.getPath().getPath() + "]: " + e.getMessage(), e );
        }

        try
        {
            if ( node == null || !node.getChildOrder().isManualOrder() )
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
            result.addError( "Error when parsing children of " + nodeFolder.getPath(), e );
        }

    }

    private boolean isNodeFolder( final VirtualFile folder )
    {
        return folder.resolve( folder.getPath().join( "_" ) ).exists();
    }

    private Node processNodeSource( final VirtualFile nodeFolder, final ProcessNodeSettings settings )
    {
        final VirtualFile nodeSource = nodeFolder.resolve( nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, NODE_XML_EXPORT_NAME ) );

        if ( !nodeSource.exists() )
        {
            throw new ImportNodeException( "Missing node source, expected at: " + nodeSource.getPath() );
        }

        final CharSource nodeCharSource;
        try
        {
            nodeCharSource = transformer == null
                ? nodeSource.getCharSource()
                : CharSource.wrap( this.transformer.transform( nodeSource.getCharSource() ) );
        }
        catch ( Exception e )
        {
            throw new ImportNodeException( "Error during XSLT pre-processing for node in '" + nodeSource.getUrl() + "'", e );
        }

        final Node.Builder newNodeBuilder = Node.create();
        try
        {
            final XmlNodeParser parser = new XmlNodeParser();
            parser.builder( newNodeBuilder );
            parser.source( nodeCharSource );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load source node [" + nodeSource.getUrl() + "]: " );
        }

        final Node newNode = newNodeBuilder.build();

        final NodePath importNodePath = NodeImportPathResolver.resolveNodeImportPath( nodeFolder, this.exportRoot, this.importRoot );

        //if node is root, then allow import only if importNodePath is also root
        if ( newNode.isRoot() )
        {
            if ( !importNodePath.isRoot() )
            {
                throw new ImportNodeException( "Cannot import root node to non-root location: " + importNodePath );
            }

            if ( nodeImportListener != null )
            {
                nodeImportListener.nodeSkipped( 1L );
            }

            result.skipped( importNodePath );

            return nodeService.getByPath( importNodePath );
        }
        else
        {
            final ImportNodeResult importNodeResult = importNode( nodeFolder, settings, newNode, importNodePath );

            if ( nodeImportListener != null )
            {
                nodeImportListener.nodeImported( 1L );
            }
            if ( importNodeResult.isPreExisting() )
            {
                result.updated( importNodeResult.getNode().path() );
            }
            else
            {
                result.added( importNodeResult.getNode().path() );
            }

            return importNodeResult.getNode();
        }
    }

    private ImportNodeResult importNode( final VirtualFile nodeFolder, final ProcessNodeSettings processNodeSettings,
                                         final Node serializedNode, final NodePath importNodePath )
    {
        final BinaryAttachments binaryAttachments = processBinaryAttachments( nodeFolder, serializedNode );

        final Node importNode = ImportNodeFactory.create()
            .importNodeIds( this.importNodeIds )
            .importPermissions( this.importPermissions )
            .serializedNode( serializedNode )
            .importPath( importNodePath )
            .manualOrderValue( processNodeSettings.getInsertManualStrategy() == InsertManualStrategy.MANUAL
                                   ? processNodeSettings.getManualOrderValue()
                                   : null )
            .build()
            .execute();

        final ImportNodeParams importNodeParams = ImportNodeParams.create()
            .importNode( importNode )
            .binaryAttachments( binaryAttachments )
            .insertManualStrategy( processNodeSettings.getInsertManualStrategy() )
            .importPermissions( this.importPermissions )
            .importPermissionsOnCreate( this.importPermissions )
            .build();

        return this.nodeService.importNode( importNodeParams );
    }

    private List<String> processBinarySource( final VirtualFile nodeFolder )
        throws Exception
    {
        final VirtualFile orderFile = nodeFolder.resolve( nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, ORDER_EXPORT_NAME ) );
        if ( !orderFile.exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected at:" + orderFile.getPath() );
        }

        return orderFile.getCharSource().readLines();
    }

    private BinaryAttachments processBinaryAttachments( final VirtualFile nodeFile, final Node newNode )
    {
        final PropertyTree data = newNode.data();

        final List<Property> binaryReferences = data.getProperties( ValueTypes.BINARY_REFERENCE );

        if ( binaryReferences.isEmpty() )
        {
            return BinaryAttachments.empty();
        }

        final BinaryAttachments.Builder builder = BinaryAttachments.create();

        for ( final Property binaryReference : binaryReferences )
        {
            addBinary( nodeFile, builder, binaryReference );
        }

        return builder.build();
    }

    private void addBinary( final VirtualFile nodeFile, final BinaryAttachments.Builder builder, final Property binaryRefProperty )
    {
        final BinaryReference binaryReference = binaryRefProperty.getBinaryReference();

        try
        {
            final VirtualFile binary = tryFindBinaryFile( nodeFile, binaryReference );

            builder.add( new BinaryAttachment( binaryReference, binary.getByteSource() ) );

            result.addBinary( binary.getPath().getPath(), binaryReference );
        }
        catch ( Exception e )
        {
            result.addError( "Error processing binary, skip", e );
        }
    }

    private VirtualFile tryFindBinaryFile( final VirtualFile nodeFile, final BinaryReference binaryReference )
    {
        final String binaryReferenceAsString = binaryReference.toString();
        final VirtualFile binaryOriginal =
            nodeFile.resolve( nodeFile.getPath().join( SYSTEM_FOLDER_NAME, BINARY_FOLDER, binaryReferenceAsString ) );

        if ( binaryOriginal.exists() )
        {
            return binaryOriginal;
        }

        // There is a chance that binaryReference is stored with UTF-8-MAC encoding (NFD-ish),
        // but export contains corresponding file with NFC coded filename
        final VirtualFile binaryNfc = nodeFile.resolve( nodeFile.getPath()
                                                            .join( SYSTEM_FOLDER_NAME, BINARY_FOLDER,
                                                                   Normalizer.normalize( binaryReferenceAsString, Normalizer.Form.NFC ) ) );
        if ( binaryNfc.exists() )
        {
            return binaryNfc;
        }

        throw new ImportNodeException( "Missing binary source, expected at: " + binaryOriginal.getPath() );
    }

    private void verifyImportRoot()
    {
        if ( importRoot.isRoot() )
        {
            return;
        }

        final Node importRoot = nodeService.getByPath( this.importRoot );

        if ( importRoot == null )
        {
            throw new ImportNodeException( "Import root '" + this.importRoot + "' not found" );
        }
    }

    private CreateNodeParams validateImportData( final CreateNodeParams createNodeParams )
    {
        CreateNodeParams validatedCreateNodeParams = createNodeParams;

        for ( final ImportValidator validator : this.importValidators )
        {
            if ( validator.canHandle( createNodeParams ) )
            {
                validatedCreateNodeParams = validator.ensureValid( validatedCreateNodeParams );
            }
        }
        return validatedCreateNodeParams;
    }

    public static final class Builder
    {
        private NodePath importRoot;

        private NodeService nodeService;

        private VirtualFile exportRoot;

        private boolean importNodeIds = true;

        private boolean importPermissions = true;

        private VirtualFile xslt;

        private Map<String, Object> xsltParams;

        private NodeImportListener nodeImportListener;

        private Builder()
        {
        }

        public Builder targetNodePath( NodePath nodePath )
        {
            this.importRoot = nodePath;
            return this;
        }

        public Builder sourceDirectory( VirtualFile exportRoot )
        {
            this.exportRoot = exportRoot;
            return this;
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder importNodeIds( final boolean importNodeIds )
        {
            this.importNodeIds = importNodeIds;
            return this;
        }

        public Builder importPermissions( final boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public Builder xslt( final VirtualFile xslt )
        {
            this.xslt = xslt;
            return this;
        }

        public Builder xsltParams( final Map<String, Object> xsltParams )
        {
            this.xsltParams = xsltParams;
            return this;
        }

        public Builder xsltParam( final String paramName, final Object paramValue )
        {
            if ( this.xsltParams == null )
            {
                this.xsltParams = new HashMap<>();
            }
            this.xsltParams.put( paramName, paramValue );
            return this;
        }

        public Builder nodeImportListener( final NodeImportListener nodeImportListener )
        {
            this.nodeImportListener = nodeImportListener;
            return this;
        }

        public NodeImporter build()
        {
            return new NodeImporter( this );
        }
    }

}
