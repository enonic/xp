package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.context.Context;

final class GetNearestSiteCommand
{
    private final ContentId contentId;

    private final ContentService contentService;

    private final Context context;

    private GetNearestSiteCommand( Builder builder )
    {
        contentId = builder.contentId;
        contentService = builder.contentService;
        context = builder.context;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final Content content = contentService.getById( this.contentId, this.context );

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

        final Content content = this.contentService.getByPath( contentPath, this.context );

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

    public static final class Builder
    {
        private ContentId contentId;

        private ContentService contentService;

        private Context context;

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

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public GetNearestSiteCommand build()
        {
            return new GetNearestSiteCommand( this );
        }
    }
}
