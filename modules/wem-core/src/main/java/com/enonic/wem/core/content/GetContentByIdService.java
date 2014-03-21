package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodeByIdService;

public class GetContentByIdService
    extends ContentService
{
    private final GetContentById command;

    public GetContentByIdService( final CommandContext context, final GetContentById command )
    {
        super( context );
        this.command = command;
    }

    public Content execute()
    {
        final EntityId entityId = EntityId.from( command.getId().toString() );

        try
        {
            final Node node = new GetNodeByIdService( session, entityId ).execute();
            return translator.fromNode( node );
        }
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( command.getId() );
        }
    }
}
