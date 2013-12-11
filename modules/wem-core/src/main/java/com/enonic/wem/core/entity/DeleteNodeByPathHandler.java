package com.enonic.wem.core.entity;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByPathHandler
    extends CommandHandler<DeleteNodeByPath>
{
    private IndexService indexService;

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    public DeleteNodeByPathHandler()
    {
    }

    public DeleteNodeByPathHandler( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.context = builder.context;
        this.command = builder.command;
    }

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Node nodeByPath = itemDao.getNodeByPath( command.getPath() );

        // TODO: How should we handle delete by path in index? For now fetch node and get Id here
        if ( nodeByPath == null )
        {
            command.setResult( DeleteNodeResult.NOT_FOUND );
            return;
        }

        try
        {
            itemDao.deleteNodeByPath( command.getPath() );
            session.save();
            command.setResult( DeleteNodeResult.SUCCESS );

            indexService.deleteEntity( nodeByPath.id() );
        }
        catch ( NoNodeAtPathFound e )
        {
            command.setResult( DeleteNodeResult.NOT_FOUND );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private CommandContext context;

        private DeleteNodeByPath command;

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder context( final CommandContext context )
        {
            this.context = context;
            return this;
        }

        public Builder command( final DeleteNodeByPath command )
        {
            this.command = command;
            return this;
        }

        public DeleteNodeByPathHandler build()
        {
            return new DeleteNodeByPathHandler( this );
        }
    }

}
