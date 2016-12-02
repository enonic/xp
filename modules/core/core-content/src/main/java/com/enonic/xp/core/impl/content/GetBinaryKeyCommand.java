package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;


final class GetBinaryKeyCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final BinaryReference binaryReference;

    private GetBinaryKeyCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.binaryReference = builder.binaryReference;
    }

    public String execute()
    {
        if ( shouldFilterScheduledPublished() )
        {
            final Node node = nodeService.getById( NodeId.from( contentId.toString() ) );
            if ( node == null || !contentPendingOrExpired( node, Instant.now() ) )
            {
                throw new ContentNotFoundException( contentId, ContextAccessor.current().getBranch() );
            }
        }
        return nodeService.getBinaryKey( NodeId.from( contentId.toString() ), binaryReference );
    }

    public static Builder create( final ContentId contentId, final BinaryReference binaryReference )
    {
        return new Builder( contentId, binaryReference );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private BinaryReference binaryReference;

        public Builder( final ContentId contentId, final BinaryReference binaryReference )
        {
            this.contentId = contentId;
            this.binaryReference = binaryReference;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentId != null, "contentId must be set" );
            Preconditions.checkNotNull( binaryReference != null, "binaryReference must be set" );
        }

        public GetBinaryKeyCommand build()
        {
            validate();
            return new GetBinaryKeyCommand( this );
        }
    }
}

