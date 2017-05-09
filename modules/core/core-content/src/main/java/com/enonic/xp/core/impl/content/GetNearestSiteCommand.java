package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.site.Site;

final class GetNearestSiteCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private GetNearestSiteCommand( Builder builder )
    {
        super( builder );
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Site execute()
    {
        final Content content = getContent( this.contentId );

        if ( content.isSite() )
        {
            return (Site) content;
        }

        return returnIfSiteOrTryParent( content.getParentPath() );
    }

    private Site returnIfSiteOrTryParent( final ContentPath contentPath )
    {
        if ( contentPath == null )
        {
            return null;
        }
        if ( contentPath.isRoot() )
        {
            return null;
        }

        final Content content = getContent( contentPath );

        if ( content.isSite() )
        {
            return (Site) content;
        }
        else
        {
            final ContentPath parentPath = content.getParentPath();
            return returnIfSiteOrTryParent( parentPath );
        }
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public GetNearestSiteCommand build()
        {
            return new GetNearestSiteCommand( this );
        }
    }
}
