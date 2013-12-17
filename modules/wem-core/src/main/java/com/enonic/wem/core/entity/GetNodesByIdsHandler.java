package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByIdsHandler
    extends CommandHandler<GetNodesByIds>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final Nodes nodes = new GetNodesByIdsService( session, this.command ).execute();

        command.setResult( nodes != null ? nodes : null );
    }
}
