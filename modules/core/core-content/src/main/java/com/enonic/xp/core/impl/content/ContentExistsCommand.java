package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;


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
            if ( contentId != null )
            {
                node = nodeService.getById( NodeId.from( contentId ) );
            }
            else
            {
                node = nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
            }
            if ( node == null )
            {
                return false;
            }

            final Content content = translator.fromNode( node, false );
            return !contentPendingOrExpired( content );
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
            Preconditions.checkArgument( contentId != null || contentPath != null, "contentId or contentPath must be given" );
        }

        public ContentExistsCommand build()
        {
            validate();
            return new ContentExistsCommand( this );
        }
    }
}

