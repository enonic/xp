package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentVersionHandler
    extends CommandHandler<GetContentVersion>
{
    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {
        final ContentVersionId versionId = command.getVersion();

        final Content content;

        if ( command.getContentId() != null )
        {
            content = contentDao.getContentVersionById( command.getContentId(), versionId, context.getJcrSession() );
        }
        else
        {
            content = contentDao.getContentVersionByPath( command.getContentPath(), versionId, context.getJcrSession() );
        }

        command.setResult( content );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
