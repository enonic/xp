package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeSearchPreference;


final class GetContentByIdCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final NodeSearchPreference searchPreference;

    private GetContentByIdCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.searchPreference = builder.searchPreference;
    }

    Content execute()
    {
        final Content content;
        final NodeId nodeId = NodeId.from( contentId );

        try
        {
            final Node node = searchPreference != null ? nodeService.getById( nodeId, searchPreference ) : nodeService.getById( nodeId );
            content = filter( ContentNodeTranslator.fromNode( node ) );
        }
        catch ( NodeNotFoundException | ContentNotFoundException e )
        {
            return null;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error getting node", e );
        }
        return content;
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

        private NodeSearchPreference searchPreference;

        Builder( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        Builder( final ContentId contentId, AbstractContentCommand source )
        {
            super( source );
            this.contentId = contentId;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        Builder searchPreference( final NodeSearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        public GetContentByIdCommand build()
        {
            validate();
            return new GetContentByIdCommand( this );
        }
    }
}
