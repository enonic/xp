package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodesByParentHandler
    extends CommandHandler<GetNodesByParent>
{
    public GetNodesByParentHandler()
    {
    }

    public GetNodesByParentHandler( final Builder builder )
    {
        this.command = builder.command;
        this.context = builder.context;
    }

    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Nodes childNodes = itemDao.getNodesByParentPath( command.getParent() );
        command.setResult( childNodes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private CommandContext context;

        private GetNodesByParent command;

        public Builder context( final CommandContext context )
        {
            this.context = context;
            return this;
        }

        public Builder command( final GetNodesByParent command )
        {
            this.command = command;
            return this;
        }

        public GetNodesByParentHandler build()
        {
            return new GetNodesByParentHandler( this );
        }
    }

}
