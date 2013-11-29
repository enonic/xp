package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentByIdsHandler
    extends CommandHandler<GetContentByIds>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final Contents result = contentDao.selectByIds( command.getIds(), context.getJcrSession() );
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
