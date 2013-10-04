package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;


public class GetContentVersionHandler
    extends CommandHandler<GetContentVersion>
{
    private ContentDao contentDao;

    @Override
    public void handle( final GetContentVersion command )
        throws Exception
    {
        final ContentSelector selector = command.getSelector();
        final ContentVersionId versionId = command.getVersion();
        final Content content = contentDao.getContentVersion( selector, versionId, context.getJcrSession() );

        command.setResult( content );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
