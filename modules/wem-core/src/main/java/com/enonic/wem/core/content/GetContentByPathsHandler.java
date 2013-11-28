package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentByPathsHandler
    extends CommandHandler<GetContentByPaths>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final Contents result = contentDao.select( command.getPaths(), context.getJcrSession() );
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
