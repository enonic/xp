package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetNodeByPathHandler
    extends CommandHandler<GetNodeByPath>
{

    public GetNodeByPathHandler()
    {
    }

    public GetNodeByPathHandler( final Builder builder )
    {
        this.command = builder.command;
        this.context = builder.context;
    }

    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao itemDao = new NodeJcrDao( session );

        final Node persistedNode;
        try
        {
            persistedNode = itemDao.getNodeByPath( command.getPath() );
            command.setResult( persistedNode );
        }
        catch ( NoNodeAtPathFound noNodeAtPathFound )
        {
            command.setResult( null );
        }

    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private CommandContext context;

        private GetNodeByPath command;

        public Builder context( final CommandContext context )
        {
            this.context = context;
            return this;
        }

        public Builder command( final GetNodeByPath command )
        {
            this.command = command;
            return this;
        }

        public GetNodeByPathHandler build()
        {
            return new GetNodeByPathHandler( this );
        }
    }


}
