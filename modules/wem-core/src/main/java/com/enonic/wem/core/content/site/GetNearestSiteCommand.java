package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;

final class GetNearestSiteCommand
{
    private ContentId contentId;

    private ContentService contentService;

    public Content execute()
    {
        final Content content = contentService.getById( this.contentId );

        if ( content.isSite() )
        {
            return content;
        }

        return returnIfSiteOrTryParent( content.getParentPath() );
    }

    private Content returnIfSiteOrTryParent( final ContentPath contentPath )
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
            return content;
        }
        else
        {
            final ContentPath parentPath = content.getParentPath();
            return returnIfSiteOrTryParent( parentPath );
        }
    }

    public GetNearestSiteCommand contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public GetNearestSiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
