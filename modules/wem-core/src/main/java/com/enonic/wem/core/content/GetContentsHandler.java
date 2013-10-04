package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentsHandler
    extends CommandHandler<GetContents>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentSelectors selectors = command.getSelectors();
        final Contents result = contentDao.select( selectors, context.getJcrSession() );
        command.setResult( result );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
