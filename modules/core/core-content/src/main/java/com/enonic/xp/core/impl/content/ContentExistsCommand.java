package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
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

    public static Builder create( final ContentId contentId )
    {
        return new Builder( contentId );
    }

    public static Builder create( final ContentPath contentPath )
    {
        return new Builder( contentPath );
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

    private Content fetchContent()
    {
        Node node;
        try
        {
            if ( contentId != null )
            {
                node = runAsAdmin( () -> nodeService.getById( NodeId.from( contentId ) ) );
            }
            else
            {
                node = runAsAdmin( () -> nodeService.getByPath( ContentNodeHelper.translateContentPathToNodePath( contentPath ) ) );
            }
        }
        catch ( NodeNotFoundException e )
        {
            return null;
        }

        try
        {
            return filter( translator.fromNode( node ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private boolean doExecute()
    {
        return fetchContent() != null;
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private ContentPath contentPath;

        Builder( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        Builder( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkArgument( contentId != null || contentPath != null, "Either contentId or contentPath is required" );
        }

        public ContentExistsCommand build()
        {
            validate();
            return new ContentExistsCommand( this );
        }
    }
}

