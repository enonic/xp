package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Objects;

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
            final Node node = nodeService.getById( NodeId.from( contentId ) );
            if ( node == null || contentPendingOrExpired( node, Instant.now() ) )
            {
                throw ContentNotFoundException.create()
                    .contentId( contentId )
                    .repositoryId( ContextAccessor.current().getRepositoryId() )
                    .branch( ContextAccessor.current().getBranch() )
                    .contentRoot( ContentNodeHelper.getContentRoot() )
                    .build();
            }
        }
        return nodeService.getBinaryKey( NodeId.from( contentId ), binaryReference );
    }

    public static Builder create( final ContentId contentId, final BinaryReference binaryReference )
    {
        return new Builder( contentId, binaryReference );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentId contentId;

        private final BinaryReference binaryReference;

        Builder( final ContentId contentId, final BinaryReference binaryReference )
        {
            this.contentId = contentId;
            this.binaryReference = binaryReference;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( contentId, "contentId is required" );
            Objects.requireNonNull( binaryReference, "binaryReference is required" );
        }

        public GetBinaryKeyCommand build()
        {
            validate();
            return new GetBinaryKeyCommand( this );
        }
    }
}

