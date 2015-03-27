package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NoNodeWithIdFoundException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.util.Exceptions;


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
            throw new ContentNotFoundException( contentId, ContextAccessor.current().getBranch() );
        }
        catch ( Exception e )
        {
            throw Exceptions.newRutime( "Error getting node" ).withCause( e );
        }
    }

    public static Builder create( final ContentId contentId, final AbstractContentCommand source )
    {
        return new Builder( contentId, source );
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

        public Builder( final ContentId contentId, AbstractContentCommand source )
        {
            super( source );
            this.contentId = contentId;
        }

        @Override
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

