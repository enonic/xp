package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodesByIdsService;


public class GetContentByIdsService
    extends ContentService
{
    private final GetContentByIds command;

    public GetContentByIdsService( final CommandContext context, final GetContentByIds command )
    {
        super( context );
        this.command = command;
    }

    public Contents execute()
    {
        final GetNodesByIds getNodesByIdsCommand = new GetNodesByIds( getAsEntityIds( command.getIds() ) );

        final Nodes nodes = new GetNodesByIdsService( session, getNodesByIdsCommand ).execute();

        return translator.fromNodes( nodes );
    }

    private EntityIds getAsEntityIds( final ContentIds contentIds )
    {
        final Set<EntityId> entityIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            entityIds.add( EntityId.from( iterator.next().toString() ) );
        }

        return EntityIds.from( entityIds );
    }

}
