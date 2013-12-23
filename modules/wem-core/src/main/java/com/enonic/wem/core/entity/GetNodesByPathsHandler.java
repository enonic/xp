package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByPathsHandler
    extends CommandHandler<GetNodesByPaths>
{
    @Override
    public void handle()
    {
        final Session session = context.getJcrSession();
        final Nodes nodes;

        try
        {
            nodes = new GetNodesByPathsService( session, this.command ).execute();
        }
        catch ( NoNodeAtPathFound ex )
        {
            final ContentPath contentPath = ContentPath.from( ex.getPath().toString() );
            throw new ContentNotFoundException( contentPath );
        }
        command.setResult( nodes );
    }
}
