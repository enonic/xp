package com.enonic.wem.core.content.site;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.content.site.GetNearestSiteByContentId;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.command.CommandHandler;

public class GetNearestSiteByContentIdHandler
    extends CommandHandler<GetNearestSiteByContentId>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetContentById getContent = Commands.content().get().byId( command.getContent() );
        final Content content = context.getClient().execute( getContent );

        if( content.isSite() )
        {
            command.setResult( content );
        }
        else
        {
            command.setResult( checkOnSite( content.getParentPath() ) );
        }
    }

    private Content checkOnSite( final ContentPath contentPath )
    {
        if( contentPath != null )
        {
            final GetContentByPath getContent = Commands.content().get().byPath( contentPath );
            final Content content = context.getClient().execute( getContent );

            if( content.isSite() )
            {
                return content;
            }
            else
            {
                final ContentPath parentPath = content.getParentPath();
                return checkOnSite( parentPath );
            }
        }

        return null;
    }
}
