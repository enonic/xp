package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;
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
        final Contents result = contentDao.select( ContentIds.from( command.getId() ), context.getJcrSession() );
        command.setResult( result.first() );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
