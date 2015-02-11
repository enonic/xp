package com.enonic.wem.repo.internal.entity;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.GetActiveNodeVersionsParams;
import com.enonic.wem.api.node.GetActiveNodeVersionsResult;
import com.enonic.wem.api.node.GetNodeVersionsParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeComparisons;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.NodeState;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.ReorderChildNodesParams;
import com.enonic.wem.api.node.ReorderChildNodesResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.node.RootNode;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.node.SyncWorkResolverParams;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.SystemConstants;
import com.enonic.wem.api.snapshot.DeleteSnapshotParams;
import com.enonic.wem.api.snapshot.DeleteSnapshotsResult;
import com.enonic.wem.api.snapshot.RestoreParams;
import com.enonic.wem.api.snapshot.RestoreResult;
import com.enonic.wem.api.snapshot.SnapshotParams;
import com.enonic.wem.api.snapshot.SnapshotResult;
import com.enonic.wem.api.snapshot.SnapshotResults;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexServiceInternal;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;
import com.enonic.wem.repo.internal.snapshot.SnapshotService;
import com.enonic.wem.repo.internal.version.VersionService;

@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private final BlobStore binaryBlobStore = new FileBlobStore( NodeConstants.binaryBlobStoreDir );

    private IndexServiceInternal indexServiceInternal;

    private NodeDao nodeDao;

    private BranchService branchService;

    private VersionService versionService;

    private QueryService queryService;

    private SnapshotService snapshotService;

    @Activate
    public void initialize()
    {
        final RepositoryInitializer repoInitializer = new RepositoryInitializer( this.indexServiceInternal );
        repoInitializer.initializeRepository( ContentConstants.CONTENT_REPO.getId() );
        repoInitializer.initializeRepository( SystemConstants.SYSTEM_REPO.getId() );
    }

    @Override
    public Node getById( final NodeId id )
    {
        final Node node = doGetById( id, true );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Node with id " + id + " not found in branch " + ContextAccessor.current().getBranch().getName() );
        }

        return node;
    }

    private Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create().
            id( id ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        return doGetByPath( path, true );
    }

    private Node doGetByPath( final NodePath path, final boolean resolveHasChild )
    {
        return GetNodeByPathCommand.create().
            nodePath( path ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        return GetNodesByIdsCommand.create().
            ids( ids ).
            resolveHasChild( true ).
            indexService( this.indexServiceInternal ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        return GetNodesByPathsCommand.create().
            paths( paths ).
            resolveHasChild( true ).
            indexService( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexService( this.indexServiceInternal ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create().
            query( nodeQuery ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
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
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexServiceInternal ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        return UpdateNodeCommand.create().
            params( params ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        return RenameNodeCommand.create().
            params( params ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public Node deleteById( final NodeId id )
    {
        return DeleteNodeByIdCommand.create().
            nodeId( id ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        return DeleteNodeByPathCommand.create().
            nodePath( path ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Branch target )
    {
        return PushNodesCommand.create().
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            ids( ids ).
            target( target ).
            build().
            execute();
    }

    @Override
    public Node duplicate( final NodeId nodeId )
    {
        return DuplicateNodeCommand.create().
            id( nodeId ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexService( this.indexServiceInternal ).
            versionService( this.versionService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    @Override
    public Node move( final NodeId nodeId, final NodePath parentNodePath )
    {
        return MoveNodeCommand.create().
            id( nodeId ).
            newParent( parentNodePath ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexService( this.indexServiceInternal ).
            versionService( this.versionService ).
            build().
            execute();
    }


    @Override
    public NodeComparison compare( final NodeId nodeId, final Branch target )
    {
        return CompareNodeCommand.create().
            nodeId( nodeId ).
            target( target ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Branch target )
    {
        return CompareNodesCommand.create().
            nodeIds( nodeIds ).
            target( target ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetNodeVersionsParams params )
    {
        return GetNodeVersionsCommand.create().
            nodeId( params.getNodeId() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        return GetActiveNodeVersionsCommand.create().
            nodeId( params.getNodeId() ).
            branches( params.getBranches() ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            indexService( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query )
    {
        return FindNodesWithVersionDifferenceCommand.create().
            versionService( this.versionService ).
            query( query ).
            build().
            execute();
    }

    @Override
    public Node getByVersionId( final NodeVersionId blobKey )
    {
        return NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( nodeDao.getByVersionId( blobKey ) );
    }

    @Override
    public ResolveSyncWorkResult resolveSyncWork( final SyncWorkResolverParams params )
    {
        return ResolveSyncWorkCommand.create().
            target( params.getBranch() ).
            nodeId( params.getNodeId() ).
            includeChildren( params.isIncludeChildren() ).
            indexService( indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            build().
            execute();
    }

    @Override
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        return SetNodeChildOrderCommand.create().
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            indexService( this.indexServiceInternal ).
            childOrder( params.getChildOrder() ).
            nodeId( params.getNodeId() ).
            build().
            execute();
    }

    @Override
    public ReorderChildNodesResult reorderChildren( final ReorderChildNodesParams params )
    {
        return ReorderChildNodesCommand.create().
            params( params ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
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
    public void deleteSnapshotRespository()
    {
        this.snapshotService.deleteSnapshotRepository();
    }

    @Override
    public int applyPermissions( final ApplyNodePermissionsParams params )
    {
        return ApplyNodePermissionsCommand.create().
            params( params ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            build().
            execute();
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        return GetBinaryCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexService( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    @Override
    public RootNode createRootNode( final CreateRootNodeParams params )
    {
        return CreateRootNodeCommand.create().
            params( params ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public Node setNodeState( final NodeId nodeId, final NodeState nodeState )
    {
        return SetNodeStateCommand.create().
            nodeId( nodeId ).
            nodeState( nodeState ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public RootNode getRoot()
    {
        final Node node = doGetByPath( NodePath.ROOT, false );

        if ( node instanceof RootNode || node == null )
        {
            return (RootNode) node;
        }

        throw new RuntimeException( "Expected node with path " + NodePath.ROOT.toString() + " to be of type RootNode, found " + node.id() );
    }

    @Override
    public boolean nodeExists( final NodeId nodeId )
    {
        return NodeHelper.runAsAdmin( () -> this.doGetById( nodeId, false ) ) != null;
    }

    @Override
    public boolean nodeExists( final NodePath nodePath )
    {
        return NodeHelper.runAsAdmin( () -> this.doGetByPath( nodePath, false ) ) != null;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    @Reference
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }

    @Reference
    public void setQueryService( final QueryService queryService )
    {
        this.queryService = queryService;
    }

    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }
}
