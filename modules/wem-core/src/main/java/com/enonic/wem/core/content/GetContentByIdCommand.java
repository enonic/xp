package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.context.Context2;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.NoEntityWithIdFoundException;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;


final class GetContentByIdCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private GetContentByIdCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
    }

    Content execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        try
        {
            final Node node = nodeService.getById( entityId );
            return translator.fromNode( node );
        }
        catch ( NoEntityWithIdFoundException | NodeNotFoundException e )
        {
            throw new ContentNotFoundException( contentId, Context2.current().getWorkspace() );
        }
        catch ( Exception e )
        {
            throw Exceptions.newRutime( "Error getting node" ).withCause( e );
        }
    }

    public static Builder create( final ContentId contentId )
    {
        return new Builder( contentId );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentId contentId;

        public Builder( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        void validate()
        {
            super.validate();
        }

        public GetContentByIdCommand build()
        {
            validate();
            return new GetContentByIdCommand( this );
        }
    }
}

