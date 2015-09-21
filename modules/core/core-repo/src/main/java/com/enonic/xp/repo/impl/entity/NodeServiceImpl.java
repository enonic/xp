package com.enonic.xp.repo.impl.entity;

import java.time.Instant;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.ImportNodeParams;
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
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.RootNode;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.file.FileBlobStore;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.entity.dao.NodeDao;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.query.QueryService;
import com.enonic.xp.repo.impl.repository.RepositoryInitializer;
import com.enonic.xp.repo.impl.snapshot.SnapshotService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.BinaryReference;

@Component(immediate = true)
public class NodeServiceImpl
    implements NodeService
{
    private final BlobStore binaryBlobStore = new FileBlobStore( NodeConstants.BINARY_BLOB_STORE_DIR );

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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create().
            query( nodeQuery ).
            indexServiceInternal( this.indexServiceInternal ).
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
        return doCreate( params, null );
    }

    private Node doCreate( final CreateNodeParams params, final Instant timestamp )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            timestamp( timestamp ).
            build().
            execute();
    }


    @Override
    public Node update( final UpdateNodeParams params )
    {
        return UpdateNodeCommand.create().
            params( params ).
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            build().
            execute();
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
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
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            binaryBlobStore( this.binaryBlobStore ).
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
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            branchService( this.branchService ).
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
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Override
    public SetNodeStateResult setNodeState( final SetNodeStateParams params )
    {
        return SetNodeStateCommand.create().
            params( params ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
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
    public Node importNode( final ImportNodeParams params )
    {
        return ImportNodeCommand.create().
            binaryAttachments( params.getBinaryAttachments() ).
            importNode( params.getNode() ).
            insertManualStrategy( params.getInsertManualStrategy() ).
            dryRun( params.isDryRun() ).
            importPermissions( params.isImportPermissions() ).
            binaryBlobStore( this.binaryBlobStore ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            build().
            execute();
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
