package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentPath contentPath = command.getPath();
        final Content result = contentDao.selectByPath( contentPath, context.getJcrSession() );

        if( result == null )
        {
            throw new ContentNotFoundException( contentPath );
        }
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
