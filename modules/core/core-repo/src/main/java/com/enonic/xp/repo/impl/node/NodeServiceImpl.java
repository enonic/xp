package com.enonic.xp.repo.impl.node;

import java.io.File;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.file.FileBlobStore;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.RepositoryInitializer;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.snapshot.SnapshotService;
import com.enonic.xp.repo.impl.storage.StorageService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private RepoConfiguration configuration;

    private IndexServiceInternal indexServiceInternal;

    private SnapshotService snapshotService;

    private StorageService storageService;

    private SearchService searchService;

    private EventPublisher eventPublisher;

    private BlobStore binaryBlobStore;

    @Activate
    public void initialize()
    {
        final File blobStoreDir = new File( configuration.getBlobStoreDir(), NodeConstants.BINARY_BLOB_STORE_DIR );
        this.binaryBlobStore = new FileBlobStore( blobStoreDir );

        final RepositoryInitializer repoInitializer = new RepositoryInitializer( this.indexServiceInternal );
        repoInitializer.initializeRepositories( ContentConstants.CONTENT_REPO.getId(), SystemConstants.SYSTEM_REPO.getId() );
    }

    @Override
    public Node getById( final NodeId id )
    {
        final Node node = doGetById( id );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Node with id " + id + " not found in branch " + ContextAccessor.current().getBranch().getName() );
        }

        return node;
    }

    private Node doGetById( final NodeId id )
    {
        return GetNodeByIdCommand.create().
            id( id ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        return doGetByPath( path );
    }

    private Node doGetByPath( final NodePath path )
    {
        return GetNodeByPathCommand.create().
            nodePath( path ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        return GetNodesByIdsCommand.create().
            ids( ids ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        return GetNodesByPathsCommand.create().
            paths( paths ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create().
            query( nodeQuery ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Node create( final CreateNodeParams params )
    {
        return doCreate( params );
    }

    private Node doCreate( final CreateNodeParams params )
    {
        final Node createdNode = CreateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( createdNode != null )
        {
            this.eventPublisher.publish( NodeEvents.created( createdNode ) );
        }
        return createdNode;
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        final Node updatedNode = UpdateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( updatedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.updated( updatedNode ) );
        }
        return updatedNode;
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        final MoveNodeResult moveNodeResult = RenameNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( moveNodeResult.getTargetNode() != null )
        {
            this.eventPublisher.publish( NodeEvents.renamed( moveNodeResult.getSourceNode(), moveNodeResult.getTargetNode() ) );
            return moveNodeResult.getTargetNode();
        }
        else
        {
            return moveNodeResult.getSourceNode();
        }
    }

    @Override
    public Node deleteById( final NodeId id )
    {
        final Node deletedNode = DeleteNodeByIdCommand.create().
            nodeId( id ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( deletedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.deleted( deletedNode ) );
        }
        return deletedNode;
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        final Node deletedNode = DeleteNodeByPathCommand.create().
            nodePath( path ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( deletedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.deleted( deletedNode ) );
        }
        return deletedNode;
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Branch target )
    {
        final PushNodesResult pushNodesResult = PushNodesCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            ids( ids ).
            target( target ).
            build().
            execute();

        if ( pushNodesResult.getSuccessful().isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.pushed( pushNodesResult.getSuccessful() ) );
        }

        return pushNodesResult;
    }

    @Override
    public Node duplicate( final NodeId nodeId )
    {
        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( duplicatedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.duplicated( duplicatedNode ) );
        }
        return duplicatedNode;
    }

    @Override
    public Node move( final NodeId nodeId, final NodePath parentNodePath )
    {
        final MoveNodeResult moveNodeResult = MoveNodeCommand.create().
            id( nodeId ).
            newParent( parentNodePath ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( moveNodeResult.getTargetNode() != null )
        {
            this.eventPublisher.publish( NodeEvents.moved( moveNodeResult.getSourceNode(), moveNodeResult.getTargetNode() ) );
            return moveNodeResult.getTargetNode();
        }
        else
        {
            return moveNodeResult.getSourceNode();
        }
    }

    @Override
    public Nodes move( final NodeIds nodeIds, final NodePath parentNodePath )
    {
        return Nodes.from( nodeIds.
            stream().
            map( nodeId -> this.move( nodeId, parentNodePath ) ).collect( Collectors.toList() ) );
    }


    @Override
    public NodeComparison compare( final NodeId nodeId, final Branch target )
    {
        return CompareNodeCommand.create().
            nodeId( nodeId ).
            target( target ).
            storageService( this.storageService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Branch target )
    {
        return CompareNodesCommand.create().
            nodeIds( nodeIds ).
            target( target ).
            storageService( this.storageService ).
            build().
            execute();
    }

    @Override
    public NodeVersionQueryResult findVersions( final GetNodeVersionsParams params )
    {
        return GetNodeVersionsCommand.create().
            nodeId( params.getNodeId() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        return GetActiveNodeVersionsCommand.create().
            nodeId( params.getNodeId() ).
            branches( params.getBranches() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }


    @Override
    public NodeVersionId setActiveVersion( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        return SetActiveVersionCommand.create().
            nodeVersionId( nodeVersionId ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public NodeVersion getByNodeVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        return this.storageService.get( nodeVersionMetadata );
    }

    @Override
    public NodeIds resolveSyncWork( final SyncWorkResolverParams params )
    {
        return ResolveSyncWorkCommand.create().
            target( params.getBranch() ).
            nodeId( params.getNodeId() ).
            includeChildren( params.isIncludeChildren() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        final Node sortedNode = SetNodeChildOrderCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            childOrder( params.getChildOrder() ).
            nodeId( params.getNodeId() ).
            build().
            execute();

        if ( sortedNode != null )
        {
            this.eventPublisher.publish( NodeEvents.sorted( sortedNode ) );
        }
        return sortedNode;
    }

    @Override
    public ReorderChildNodesResult reorderChildren( final ReorderChildNodesParams params )
    {
        final ReorderChildNodesResult reorderChildNodesResult = ReorderChildNodesCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        for ( Node parentNode : reorderChildNodesResult.getParentNodes() )
        {
            this.eventPublisher.publish( NodeEvents.sorted( parentNode ) );
        }

        return reorderChildNodesResult;
    }

    @Override
    public SnapshotResult snapshot( final SnapshotParams params )
    {
        return this.snapshotService.snapshot( params );
    }

    @Override
    public RestoreResult restore( final RestoreParams params )
    {
        return this.snapshotService.restore( params );
    }

    @Override
    public DeleteSnapshotsResult deleteSnapshot( final DeleteSnapshotParams params )
    {
        return this.snapshotService.delete( params );
    }

    @Override
    public SnapshotResults listSnapshots()
    {
        return this.snapshotService.list();
    }

    @Override
    public void refresh( final RefreshMode refreshMode )
    {
        RefreshCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            refreshMode( refreshMode ).
            build().
            execute();
    }

    @Override
    public void deleteSnapshotRespository()
    {
        this.snapshotService.deleteSnapshotRepository();
    }

    @Override
    public int applyPermissions( final ApplyNodePermissionsParams params )
    {
        final Nodes updatedNodes = ApplyNodePermissionsCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        for ( final Node node : updatedNodes )
        {
            this.eventPublisher.publish( NodeEvents.updated( node ) );
        }

        return updatedNodes.getSize();
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        return GetBinaryCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public String getBinaryKey( final NodeId nodeId, final BinaryReference reference )
    {
        return GetBinaryKeyCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    @Override
    public Node createRootNode( final CreateRootNodeParams params )
    {
        final Node createdNode = CreateRootNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( createdNode != null )
        {
            this.eventPublisher.publish( NodeEvents.created( createdNode ) );
        }
        return createdNode;
    }

    @Override
    public SetNodeStateResult setNodeState( final SetNodeStateParams params )
    {
        final SetNodeStateResult setNodeStateResult = SetNodeStateCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( setNodeStateResult.getUpdatedNodes().isNotEmpty() )
        {
            this.eventPublisher.publish( NodeEvents.stateUpdated( setNodeStateResult.getUpdatedNodes() ) );
        }

        return setNodeStateResult;
    }

    @Override
    public Node getRoot()
    {
        final Node node = doGetByPath( NodePath.ROOT );

        if ( node == null || node.isRoot() )
        {
            return node;
        }

        throw new RuntimeException( "Expected node with path " + NodePath.ROOT.toString() + " to be of type RootNode, found " + node.id() );
    }

    @Override
    public ImportNodeResult importNode( final ImportNodeParams params )
    {
        final ImportNodeResult importNodeResult = ImportNodeCommand.create().
            binaryAttachments( params.getBinaryAttachments() ).
            importNode( params.getNode() ).
            insertManualStrategy( params.getInsertManualStrategy() ).
            dryRun( params.isDryRun() ).
            importPermissions( params.isImportPermissions() ).
            binaryBlobStore( this.binaryBlobStore ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        if ( importNodeResult.isPreExisting() )
        {
            this.eventPublisher.publish( NodeEvents.updated( importNodeResult.getNode() ) );
        }
        else
        {
            this.eventPublisher.publish( NodeEvents.created( importNodeResult.getNode() ) );
        }

        return importNodeResult;
    }

    @Override
    public boolean nodeExists( final NodeId nodeId )
    {
        return NodeHelper.runAsAdmin( () -> this.doGetById( nodeId ) ) != null;
    }

    @Override
    public boolean nodeExists( final NodePath nodePath )
    {
        return NodeHelper.runAsAdmin( () -> this.doGetByPath( nodePath ) ) != null;
    }

    @Override
    public NodesHasChildrenResult hasChildren( final Nodes nodes )
    {
        return NodeHasChildResolver.create().
            searchService( this.searchService ).
            build().
            resolve( nodes );
    }

    @Override
    public boolean hasChildren( final Node node )
    {
        return NodeHasChildResolver.create().
            searchService( this.searchService ).
            build().
            resolve( node );
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Reference
    public void setStorageService( final StorageService storageService )
    {
        this.storageService = storageService;
    }

    @Reference
    public void setSearchService( final SearchService searchService )
    {
        this.searchService = searchService;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    @Reference
    public void setConfiguration( final RepoConfiguration configuration )
    {
        this.configuration = configuration;
    }
}
