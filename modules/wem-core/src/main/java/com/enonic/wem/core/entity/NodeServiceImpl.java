package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

public class NodeServiceImpl
    implements NodeService
{
    private IndexService indexService;

    private NodeDao nodeDao;

    private WorkspaceService workspaceService;

    private VersionService versionService;

    private QueryService queryService;

    @Override
    public Node getById( final EntityId id )
    {
        final Context context = Context.current();

        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( id, WorkspaceContext.from( context ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in workspace " + context.getWorkspace().getName() );
        }

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( currentVersion ) );
    }

    @Override
    public Nodes getByIds( final EntityIds ids )
    {
        final NodeVersionIds versionIds = this.workspaceService.getByVersionIds( ids, WorkspaceContext.from( Context.current() ) );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) );
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        final Context context = Context.current();

        final NodeVersionId currentVersion = this.workspaceService.getByPath( path, WorkspaceContext.from( context ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with path " + path + " not found in workspace " + context.getWorkspace().getName() );
        }

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( currentVersion ) );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        final NodeVersionIds versionIds = this.workspaceService.getByPaths( paths, WorkspaceContext.from( Context.current() ) );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) );
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
            build().
            execute();
    }

    @Override
    public Node create( final CreateNodeParams params )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
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
            build().
            execute();
    }

    @Override
    public Node deleteById( final EntityId id )
    {
        return DeleteNodeByIdCommand.create().
            entityId( id ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
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
            build().
            execute();
    }

    @Override
    public Node push( final EntityId id, final Workspace target )
    {
        return PushNodeCommand.create().
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            id( id ).
            target( target ).
            build().
            execute();
    }

    @Override
    public NodeComparison compare( final EntityId entityId, final Workspace target )
    {
        return CompareNodeCommand.create().
            entityId( entityId ).
            target( target ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final EntityIds entityIds, final Workspace target )
    {
        return CompareNodesCommand.create().
            entityIds( entityIds ).
            target( target ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetNodeVersionsParams params )
    {
        return GetEntityVersionsCommand.create().
            entityId( params.getEntityId() ).
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
            entityId( params.getEntityId() ).
            workspaces( params.getWorkspaces() ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }

    @Override
    public Node getByVersionId( final NodeVersionId blobKey )
    {
        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( blobKey ) );
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
