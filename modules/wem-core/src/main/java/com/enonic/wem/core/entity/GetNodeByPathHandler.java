package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodeByPathHandler
    extends CommandHandler<GetNodeByPath>
{

    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();

        final Node persistedNode;
        try
        {
            persistedNode = new GetNodeByPathService( session, command ).execute();
            command.setResult( persistedNode );
        }
        catch ( NoNodeAtPathFound noNodeAtPathFound )
        {
            command.setResult( null );
        }

    }
}
