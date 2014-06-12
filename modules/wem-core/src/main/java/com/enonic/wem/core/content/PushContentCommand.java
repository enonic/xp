package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;

public class PushContentCommand
    extends AbstractContentCommand
{

    private final ContentId contentId;

    private final Workspace to;

    private PushContentCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.to = builder.to;
    }

    Content execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        final Node pushedNode = nodeService.push( entityId, this.to, this.context );

        return getTranslator().fromNode( pushedNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private Workspace to;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder to( final Workspace to )
        {
            this.to = to;
            return this;
        }

        public PushContentCommand build()
        {
            validate();
            return new PushContentCommand( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( to );
            Preconditions.checkNotNull( contentId );
        }
    }

}
