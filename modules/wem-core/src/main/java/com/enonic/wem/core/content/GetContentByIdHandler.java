package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.content.Content;
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
        final Content result = contentDao.selectById( command.getId(), context.getJcrSession() );
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
