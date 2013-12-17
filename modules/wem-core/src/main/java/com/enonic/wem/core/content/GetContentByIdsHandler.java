package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodesByIdsService;


public class GetContentByIdsHandler
    extends CommandHandler<GetContentByIds>
{
    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final GetNodesByIds getNodesByIdsCommand = new GetNodesByIds( getAsEntityIds( command.getIds() ) );

        final Nodes nodes = new GetNodesByIdsService( this.context.getJcrSession(), getNodesByIdsCommand ).execute();

        command.setResult( CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes ) );
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
