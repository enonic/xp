package com.enonic.wem.core.entity;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityComparisons;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.EntityVersions;
import com.enonic.wem.api.entity.GetEntityVersionsParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.compare.WorkspaceCompareService;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private ElasticsearchIndexService indexService;

    @Inject
    private NodeDao nodeDao;

    @Inject
    private WorkspaceCompareService workspaceCompareService;

    @Inject
    private VersionService versionService;

    @Override
    public Node getById( final EntityId id, final Context context )
    {
        return nodeDao.getById( id, context.getWorkspace() );
    }

    @Override
    public Nodes getByIds( final EntityIds ids, final Context context )
    {
        return nodeDao.getByIds( ids, context.getWorkspace() );
    }

    @Override
    public Node getByPath( final NodePath path, final Context context )
    {
        return nodeDao.getByPath( path, context.getWorkspace() );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths, final Context context )
    {
        return nodeDao.getByPaths( paths, context.getWorkspace() );
    }

    @Override
    public Nodes getByParent( final NodePath parent, final Context context )
    {
        return nodeDao.getByParent( parent, context.getWorkspace() );
    }

    @Override
    public Node create( final CreateNodeParams params, final Context context )
    {
        return CreateNodeCommand.create( context ).
            params( params ).
            indexService( this.indexService ).
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
            build().
            execute();

    }

    @Override
    public Node push( final EntityId id, final Workspace target, final Context context )
    {
        return PushNodeCommand.create( context ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            id( id ).
            target( target ).
            build().
            execute();
    }


    @Override
    public EntityComparison compare( final EntityId id, final Workspace target, final Context context )
    {
        return CompareNodeCommand.create( context ).
            id( id ).
            target( target ).
            compareService( workspaceCompareService ).
            build().
            execute();
    }

    @Override
    public EntityComparisons compare( final EntityIds ids, final Workspace target, final Context context )
    {
        return CompareNodesCommand.create( context ).
            ids( ids ).
            target( target ).
            compareService( workspaceCompareService ).
            build().
            execute();
    }

    @Override
    public EntityVersions getVersions( final GetEntityVersionsParams params, final Context context )
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
    public Node getByBlobKey( final BlobKey blobKey, final Context context )
    {
        return nodeDao.getByBlobKey( blobKey );
    }
}
