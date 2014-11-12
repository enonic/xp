package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;

public class PushContentCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final Workspace target;

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.target = builder.target;
    }

    Content execute()
    {
        final NodeId nodeId = NodeId.from( contentId.toString() );

        final Node pushedNode = nodeService.push( nodeId, this.target );

        return translator.fromNode( pushedNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private Workspace target;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentId );
        }

        public PushContentCommand build()
        {
            validate();
            return new PushContentCommand( this );
        }

    }

}
