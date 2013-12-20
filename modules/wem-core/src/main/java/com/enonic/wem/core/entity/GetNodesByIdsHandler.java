package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
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
        final Nodes nodes;

        try
        {
            nodes = new GetNodesByIdsService( session, this.command ).execute();
        }
        catch ( NoEntityWithIdFound ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId );

        }
        command.setResult( nodes );
    }
}
