package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
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
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.OrderChildNodeParams;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.node.UpdateNodeParams;

public class NodeServiceImpl
    implements NodeService
{
    private IndexService indexService;

    private NodeDao nodeDao;

    private WorkspaceService workspaceService;

    private VersionService versionService;

    private QueryService queryService;

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
        final Node node = doGetByPath( path, true );

        if ( node == null )
        {
            throw new NodeNotFoundException(
                "Node with path " + path + " not found in workspace " + ContextAccessor.current().getWorkspace().getName() );
        }

        return node;
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
    public Node push( final NodeId id, final Workspace target )
    {
        return PushNodeCommand.create().
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            id( id ).
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
    public NodeComparison compare( final NodeId nodeId, final Workspace target )
    {
        return CompareNodeCommand.create().
            nodeId( nodeId ).
            target( target ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final NodeIds nodeIds, final Workspace target )
    {
        return CompareNodesCommand.create().
            nodeId( nodeIds ).
            target( target ).
            queryService( this.queryService ).
            versionService( this.versionService ).
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
    public Node moveChild( final OrderChildNodeParams params )
    {
        final Node nodeToMove = doGetById( params.getNodeId(), false );
        final Node nodeToMoveBefore = params.getMoveBefore() == null ? null : doGetById( params.getMoveBefore(), false );
        final Node parentNode = doGetByPath( nodeToMove.parent(), false );

        return MoveChildNodeCommand.create().
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
            parentNode( parentNode ).
            nodeToMove( nodeToMove ).
            nodeToMoveBefore( nodeToMoveBefore ).
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
