package com.enonic.wem.core.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityId;
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

    void execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        nodeService.push( entityId, this.to, this.context );
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
