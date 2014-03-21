package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.util.Exceptions;

public class DeleteNodeByPathCommand
{
    private IndexService indexService;

    private Session session;

    private DeleteNodeByPathParams params;

    public DeleteNodeByPathCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.session = builder.session;
        this.params = builder.params;
    }

    public Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error deleting node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final NodePath nodePath = params.getPath();
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node nodeToDelete = nodeJcrDao.getNodeByPath( nodePath );

        nodeJcrDao.deleteNodeByPath( nodePath );
        session.save();

        indexService.deleteEntity( nodeToDelete.id() );
        return nodeToDelete;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private Session session;

        private DeleteNodeByPathParams params;

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder session( final Session session )
        {
            this.session = session;
            return this;
        }

        public Builder params( final DeleteNodeByPathParams params )
        {
            this.params = params;
            return this;
        }

        public DeleteNodeByPathCommand build()
        {
            return new DeleteNodeByPathCommand( this );
        }
    }

}
