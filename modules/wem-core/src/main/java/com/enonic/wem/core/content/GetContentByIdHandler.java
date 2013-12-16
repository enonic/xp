package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentByIdHandler
    extends CommandHandler<GetContentById>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentId contentId = command.getId();
        final Content result = contentDao.selectById( contentId, context.getJcrSession() );

        if( result == null )
        {
            throw new ContentNotFoundException( contentId );
        }
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
