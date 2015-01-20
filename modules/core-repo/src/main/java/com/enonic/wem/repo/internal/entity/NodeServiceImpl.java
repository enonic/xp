package com.enonic.wem.repo.internal.entity;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.CreateNodeParams;
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
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.ReorderChildNodesParams;
import com.enonic.wem.api.node.ReorderChildNodesResult;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexService;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

public class NodeServiceImpl
    implements NodeService
{
    private IndexService indexService;

    private NodeDao nodeDao;

    private WorkspaceService workspaceService;

    private VersionService versionService;

    private QueryService queryService;

    private final BlobStore binaryBlobStore = new FileBlobStore( NodeConstants.binaryBlobStoreDir );

    @Override
    public Node getById( final NodeId id )
    {
        final Node node = doGetById( id, true );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Node with id " + id + " not found in workspace " + ContextAccessor.current().getWorkspace().getName() );
        }

        return node;
    }

    private Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create().
            id( id ).
            resolveHasChild( resolveHasChild ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        return GetNodesByPathsCommand.create().
            paths( paths ).
            resolveHasChild( true ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
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
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return FindNodesByQueryCommand.create().
            query( nodeQuery ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Workspace target )
    {
        return PushNodesCommand.create().
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
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
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            build().
            execute();
    }


    @Override
    public NodeComparison compare( final NodeId nodeId, final Workspace target )
    {
        return CompareNodeCommand.create().
            nodeId( nodeId ).
            target( target ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Workspace target )
    {
        return CompareNodesCommand.create().
            nodeId( nodeIds ).
            target( target ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
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
            workspaces( params.getWorkspaces() ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();
    }

    @Override
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query )
    {
        return NodeVersionDiffCommand.create().
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
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        return SetNodeChildOrderCommand.create().
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
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
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public void snapshot()
    {
        this.indexService.snapshot( ContextAccessor.current().getRepositoryId() );
    }

    @Override
    public int applyPermissions( final ApplyNodePermissionsParams params )
    {
        return ApplyNodePermissionsCommand.create().
            params( params ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        return GetBinaryCommand.create().
            binaryReference( reference ).
            nodeId( nodeId ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    public void setWorkspaceService( final WorkspaceService workspaceService )
    {
        this.workspaceService = workspaceService;
    }

    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }

    public void setQueryService( final QueryService queryService )
    {
        this.queryService = queryService;
    }
}
