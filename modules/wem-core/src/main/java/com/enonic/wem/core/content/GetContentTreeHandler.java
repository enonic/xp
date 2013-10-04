package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentTreeHandler
    extends CommandHandler<GetContentTree>
{
    private ContentDao contentDao;

    @Override
    public void handle( final GetContentTree command )
        throws Exception
    {
        if ( command.getContentSelectors() != null )
        {
            command.setResult( contentDao.getContentTree( context.getJcrSession(), command.getContentSelectors() ) );
        }
        else
        {
            command.setResult( contentDao.getContentTree( context.getJcrSession() ) );
        }
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
