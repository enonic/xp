package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.util.Exceptions;


final class GetContentByIdCommand
    extends AbstractContentCommand<GetContentByIdCommand>
{
    private ContentId contentId;

    Content execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        try
        {
            final Node node = nodeService.getById( entityId, new Context( ContentConstants.DEFAULT_WORKSPACE ) );
            return getTranslator().fromNode( node );
        }
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( contentId );
        }
        catch ( Exception e )
        {
            throw Exceptions.newRutime( "Error getting node" ).withCause( e );
        }
    }

    GetContentByIdCommand contentId( final ContentId id )
    {
        this.contentId = id;
        return this;
    }
}
