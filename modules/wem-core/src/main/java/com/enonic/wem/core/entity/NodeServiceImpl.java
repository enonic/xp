package com.enonic.wem.core.entity;

import javax.inject.Inject;

import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
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

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private ElasticsearchIndexService indexService;

    @Inject
    private NodeDao nodeDao;

    @Override
    public Node getById( final EntityId id, final Workspace workspace )
    {
        return nodeDao.getById( id, workspace );
    }

    @Override
    public Nodes getByIds( final EntityIds ids, final Workspace workspace )
    {
        return nodeDao.getByIds( ids, workspace );
    }

    @Override
    public Node getByPath( final NodePath path, final Workspace workspace )
    {
        return nodeDao.getByPath( path, workspace );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths, final Workspace workspace )
    {
        return nodeDao.getByPaths( paths, workspace );
    }

    @Override
    public Nodes getByParent( final NodePath parent, final Workspace workspace )
    {
        return nodeDao.getByParent( parent, workspace );
    }

    @Override
    public Node create( final CreateNodeParams params )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
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
            build().
            execute();
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        return new RenameNodeCommand().
            params( params ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            workspace( params.getWorkspace() ).
            execute();
    }

    @Override
    public Node deleteById( final EntityId id, final Workspace workspace )
    {
        return DeleteNodeByIdCommand.create().
            entityId( id ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }

    @Override
    public Node deleteByPath( final NodePath path, final Workspace workspace )
    {
        return DeleteNodeByPathCommand.create().
            nodePath( path ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            build().
            execute();

    }
}
