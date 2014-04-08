package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.GetNearestSiteByContentId;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.core.command.CommandHandler;

public class GetNearestSiteByContentIdHandler
    extends CommandHandler<GetNearestSiteByContentId>
{
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final Content content = contentService.getById( command.getContent() );

        if ( content.isSite() )
        {
            command.setResult( content );
        }
        else
        {
            command.setResult( returnIfSiteOrTryParent( content.getParentPath() ) );
        }
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

        final Content content = contentService.getByPath( contentPath );

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

    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
