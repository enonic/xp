package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodeByPathHandler
    extends CommandHandler<GetNodeByPath>
{

    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();

        final Node persistedNode = new GetNodeByPathService( session, command ).execute();
        command.setResult( persistedNode );
    }
}
