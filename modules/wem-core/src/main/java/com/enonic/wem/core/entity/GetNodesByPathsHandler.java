package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

import static com.enonic.wem.api.entity.Nodes.newNodes;

public class GetNodesByPathsHandler
    extends CommandHandler<GetNodesByPaths>
{

    public GetNodesByPathsHandler()
    {
    }

    public GetNodesByPathsHandler( final Builder builder )
    {
        context = builder.context;
        command = builder.command;
    }


    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();
        final NodeJcrDao nodeDao = new NodeJcrDao( session );
        final Nodes.Builder nodes = newNodes();
        for ( final NodePath path : command.getPaths() )
        {
            try
            {
                nodes.add( nodeDao.getNodeByPath( path ) );
            }
            catch ( NoNodeAtPathFound noNodeAtPathFound )
            {
                // Node not found, just continue for now
            }
        }

        command.setResult( nodes.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private CommandContext context;

        private GetNodesByPaths command;

        public Builder context( final CommandContext context )
        {
            this.context = context;
            return this;
        }

        public Builder command( final GetNodesByPaths command )
        {
            this.command = command;
            return this;
        }

        public GetNodesByPathsHandler build()
        {
            return new GetNodesByPathsHandler( this );
        }
    }

}
