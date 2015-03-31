package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

import static com.enonic.xp.core.impl.content.ContentNodeHelper.translateNodePathToContentPath;


final class SortContentCommand
    extends AbstractContentCommand
{
    private final SortContentParams params;

    private SortContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );
        final Node existingNode = nodeService.getById( nodeId );

        final Content content = getContent( params.getContentId() );

        final ContentChangeEvent event = ContentChangeEvent.create().
            change( ContentChangeEvent.ContentChangeType.SORT, translateNodePathToContentPath( existingNode.path() ) ).
            build();
        eventPublisher.publish( event );

        return content;
    }

    public static Builder create( final SortContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final SortContentParams params;

        public Builder( final SortContentParams params )
        {
            this.params = params;
        }

        void validate()
        {
            super.validate();
        }

        public SortContentCommand build()
        {
            validate();
            return new SortContentCommand( this );
        }

    }


}

