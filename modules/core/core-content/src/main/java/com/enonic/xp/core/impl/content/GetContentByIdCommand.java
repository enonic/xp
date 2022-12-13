package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
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
        final Content content;
        final NodeId nodeId = NodeId.from( contentId );

        try
        {
            final Node node = nodeService.getById( nodeId );
            content = filter( translator.fromNode( node, true ) );
        }
        catch ( NodeNotFoundException | ContentNotFoundException e )
        {
            return null;
        }
        catch ( Exception e )
        {
            throw Exceptions.newRuntime( "Error getting node" ).withCause( e );
        }
        return content;
    }

    public static Builder create( final ContentId contentId )
    {
        return new Builder( contentId );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentId contentId;

        Builder( final ContentId contentId )
        {
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

