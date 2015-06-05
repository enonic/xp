package com.enonic.xp.core.impl.site;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.site.Site;

final class GetNearestSiteCommand
{
    private final ContentId contentId;

    private final ContentService contentService;

    private GetNearestSiteCommand( Builder builder )
    {
        contentId = builder.contentId;
        contentService = builder.contentService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Site execute()
    {
        final Content content = contentService.getById( this.contentId );

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

        final Content content = this.contentService.getByPath( contentPath );

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
    {
        private ContentId contentId;

        private ContentService contentService;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public GetNearestSiteCommand build()
        {
            return new GetNearestSiteCommand( this );
        }
    }
}
