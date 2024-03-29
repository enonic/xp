package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;


final class GetBinaryCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final BinaryReference binaryReference;

    private GetBinaryCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.binaryReference = builder.binaryReference;
    }

    public ByteSource execute()
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
        return nodeService.getBinary( NodeId.from( contentId ), binaryReference );
    }

    public static Builder create( final ContentId contentId, final BinaryReference binaryReference )
    {
        return new Builder( contentId, binaryReference );
    }

    public static Builder create( final ContentId contentId, final BinaryReference binaryReference, final AbstractContentCommand source )
    {
        return new Builder( contentId, binaryReference, source );
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

        Builder( final ContentId contentId, final BinaryReference binaryReference, AbstractContentCommand source )
        {
            super( source );
            this.contentId = contentId;
            this.binaryReference = binaryReference;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentId, "contentId must be set" );
            Preconditions.checkNotNull( binaryReference, "binaryReference must be set" );
        }

        public GetBinaryCommand build()
        {
            validate();
            return new GetBinaryCommand( this );
        }
    }
}

