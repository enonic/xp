package com.enonic.wem.core.entity;


import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.entity.CreateNode;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class CreateNodeHandler
    extends CommandHandler<CreateNode>
{
    private IndexService indexService;

    public CreateNodeHandler()
    {
    }

    private CreateNodeHandler( final Builder builder )
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
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final CreateNodeArguments createNodeArguments = CreateNodeArguments.newCreateNodeArgs().
            creator( UserKey.superUser() ).
            parent( command.getParent() ).
            name( command.getName() ).
            rootDataSet( command.getData() ).
            entityIndexConfig( command.getEntityIndexConfig() ).
            build();

        final Node persistedNode = nodeJcrDao.createNode( createNodeArguments );
        session.save();

        command.setResult( new CreateNodeResult( persistedNode ) );

        indexService.indexNode( persistedNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    public static class Builder
    {
        private IndexService indexService;

        private CommandContext context;

        private CreateNode command;

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

        public Builder command( final CreateNode command )
        {
            this.command = command;
            return this;
        }

        public CreateNodeHandler build()
        {
            return new CreateNodeHandler( this );
        }
    }
}


