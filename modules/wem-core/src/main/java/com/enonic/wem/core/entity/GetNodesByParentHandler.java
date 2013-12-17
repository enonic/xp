package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByParentHandler
    extends CommandHandler<GetNodesByParent>
{
    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();

        final Nodes childNodes = new GetNodesByParentService( session, this.command ).execute();

        command.setResult( childNodes );
    }
}
