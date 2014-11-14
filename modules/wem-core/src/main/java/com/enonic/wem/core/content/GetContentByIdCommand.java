package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.NoNodeWithIdFoundException;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeNotFoundException;


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
        final NodeId nodeId = NodeId.from( contentId.toString() );

        try
        {
            final Node node = nodeService.getById( nodeId );
            return translator.fromNode( node );
        }
        catch ( NoNodeWithIdFoundException | NodeNotFoundException e )
        {
            throw new ContentNotFoundException( contentId, ContextAccessor.current().getWorkspace() );
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

