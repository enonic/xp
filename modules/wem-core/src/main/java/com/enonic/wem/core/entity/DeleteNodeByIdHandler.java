package com.enonic.wem.core.entity;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeById;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByIdHandler
    extends CommandHandler<DeleteNodeById>
{
    private IndexService indexService;

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }


    public DeleteNodeByIdHandler()
    {
    }

    public DeleteNodeByIdHandler( final Builder builder )
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

        try
        {
            itemDao.deleteNodeById( command.getId() );
            session.save();
            command.setResult( DeleteNodeResult.SUCCESS );

            indexService.deleteEntity( command.getId() );
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

        private DeleteNodeById command;

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

        public Builder command( final DeleteNodeById command )
        {
            this.command = command;
            return this;
        }

        public DeleteNodeByIdHandler build()
        {
            return new DeleteNodeByIdHandler( this );
        }
    }

}
