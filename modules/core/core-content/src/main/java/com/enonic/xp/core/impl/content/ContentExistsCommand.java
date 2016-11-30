package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;


final class ContentExistsCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private final ContentPath contentPath;

    private ContentExistsCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
        this.contentPath = builder.contentPath;
    }

    boolean execute()
    {
        try
        {
            return doExecute();
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
    }

    private boolean doExecute()
    {
        if ( shouldFilterScheduledPublished() )
        {
            final Node node;
            try
            {
                if ( contentId != null )
                {
                    node = nodeService.getById( NodeId.from( contentId ) );
                }
                else
                {
                    node = nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
                }
            }
            catch ( NodeNotFoundException e )
            {
                return false;
            }
            if ( node == null )
            {
                return false;
            }
            return !contentPendingOrExpired( node, Instant.now() );
        }
        else
        {
            if ( contentId != null )
            {
                return nodeService.nodeExists( NodeId.from( contentId ) );
            }
            else
            {
                return nodeService.nodeExists( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
            }
        }
    }

    public static Builder create( final ContentId contentId )
    {
        return new Builder( contentId );
    }

    public static Builder create( final ContentPath contentPath )
    {
        return new Builder( contentPath );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private ContentPath contentPath;

        public Builder( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        public Builder( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( contentId != null || contentPath != null, "Either contentId or contentPath must be set" );
        }

        public ContentExistsCommand build()
        {
            validate();
            return new ContentExistsCommand( this );
        }
    }
}

