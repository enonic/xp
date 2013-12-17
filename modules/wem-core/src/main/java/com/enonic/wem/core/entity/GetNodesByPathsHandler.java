package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByPathsHandler
    extends CommandHandler<GetNodesByPaths>
{
    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();

        final Nodes nodes = new GetNodesByPathsService( session, this.command ).execute();

        command.setResult( nodes );
    }
}
