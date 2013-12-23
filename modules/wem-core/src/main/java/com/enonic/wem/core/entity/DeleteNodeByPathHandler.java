package com.enonic.wem.core.entity;

import javax.inject.Inject;

import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
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
        command.setResult( new DeleteNodeByPathService( context.getJcrSession(), indexService, command ).execute() );
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
