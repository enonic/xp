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
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;
import com.enonic.wem.core.index.IndexService;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    @Inject
    private NodeElasticsearchDao nodeElasticsearchDao;

    @Override
    public Node getById( final EntityId id )
    {
        return nodeElasticsearchDao.getById( id );
    }

    @Override
    public Nodes getByIds( final EntityIds ids )
    {
        return nodeElasticsearchDao.getByIds( ids );
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        return nodeElasticsearchDao.getByPath( path );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        return nodeElasticsearchDao.getByPaths( paths );
    }

    @Override
    public Nodes getByParent( final NodePath parent )
    {
        return nodeElasticsearchDao.getByParent( parent );
    }

    @Override
    public Node create( final CreateNodeParams params )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            build().
            execute();
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        return UpdateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            build().
            execute();
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        return new RenameNodeCommand().
            params( params ).
            indexService( this.indexService ).
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            execute();
    }

    @Override
    public Node deleteById( final EntityId id )
    {
        return DeleteNodeByIdCommand.create().
            entityId( id ).
            indexService( this.indexService ).
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            build().
            execute();
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        return DeleteNodeByPathCommand.create().
            nodePath( path ).
            indexService( this.indexService ).
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            build().
            execute();

    }
}
