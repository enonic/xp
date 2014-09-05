package com.enonic.wem.core.entity;

import javax.inject.Inject;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.FindNodesByParentParams;
import com.enonic.wem.api.entity.FindNodesByParentResult;
import com.enonic.wem.api.entity.FindNodesByQueryResult;
import com.enonic.wem.api.entity.GetActiveNodeVersionsParams;
import com.enonic.wem.api.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.api.entity.GetNodeVersionsParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeComparison;
import com.enonic.wem.api.entity.NodeComparisons;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.WorkspaceCompareService;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    @Inject
    private NodeDao nodeDao;

    @Inject
    WorkspaceService workspaceService;

    @Inject
    private WorkspaceCompareService workspaceCompareService;

    @Inject
    private VersionService versionService;

    @Inject
    private QueryService queryService;

    @Override
    public Node getById( final EntityId id, final Context context )
    {
        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( new WorkspaceIdQuery( context.getWorkspace(), id ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in workspace " + context.getWorkspace().getName() );
        }

        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( currentVersion ) );
    }

    @Override
    public Nodes getByIds( final EntityIds ids, final Context context )
    {
        final NodeVersionIds versionIds = this.workspaceService.getByVersionIds( new WorkspaceIdsQuery( context.getWorkspace(), ids ) );

        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) );
    }

    @Override
    public Node getByPath( final NodePath path, final Context context )
    {
        final NodeVersionId currentVersion = this.workspaceService.getByPath( new WorkspacePathQuery( context.getWorkspace(), path ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with path " + path + " not found in workspace " + context.getWorkspace().getName() );
        }

        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( currentVersion ) );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths, final Context context )
    {
        final NodeVersionIds versionIds = this.workspaceService.getByPaths( new WorkspacePathsQuery( context.getWorkspace(), paths ) );

        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionIds( versionIds ) );
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params, final Context context )
    {
        return FindNodesByParentCommand.create( context ).
            params( params ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery, final Context context )
    {
        return FindNodesByQueryCommand.create( context ).
            query( nodeQuery ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    @Override
    public Node create( final CreateNodeParams params, final Context context )
    {
        return CreateNodeCommand.create( context ).
            params( params ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }

    @Override
    public Node update( final UpdateNodeParams params, final Context context )
    {
        return UpdateNodeCommand.create( context ).
            params( params ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public Node rename( final RenameNodeParams params, final Context context )
    {
        return RenameNodeCommand.create( context ).
            params( params ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public Node deleteById( final EntityId id, final Context context )
    {
        return DeleteNodeByIdCommand.create( context ).
            entityId( id ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public Node deleteByPath( final NodePath path, final Context context )
    {
        return DeleteNodeByPathCommand.create( context ).
            nodePath( path ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public Node push( final EntityId id, final Workspace target, final Context context )
    {
        return PushNodeCommand.create( context ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            id( id ).
            target( target ).
            build().
            execute();
    }


    @Override
    public NodeComparison compare( final EntityId id, final Workspace target, final Context context )
    {
        return CompareNodeCommand.create( context ).
            id( id ).
            target( target ).
            compareService( workspaceCompareService ).
            build().
            execute();
    }

    @Override
    public NodeComparisons compare( final EntityIds ids, final Workspace target, final Context context )
    {
        return CompareNodesCommand.create( context ).
            ids( ids ).
            target( target ).
            compareService( workspaceCompareService ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetNodeVersionsParams params, final Context context )
    {
        return GetEntityVersionsCommand.create( context ).
            entityId( params.getEntityId() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params, final Context context )
    {
        return GetActiveNodeVersionsCommand.create( context ).
            entityId( params.getEntityId() ).
            workspaces( params.getWorkspaces() ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }

    @Override
    public Node getByVersionId( final NodeVersionId blobKey, final Context context )
    {
        return NodeHasChildResolver.create().
            workspace( context.getWorkspace() ).
            workspaceService( this.workspaceService ).
            build().
            resolve( nodeDao.getByVersionId( blobKey ) );
    }
}
