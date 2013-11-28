package com.enonic.wem.core.content;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{

    private ContentDao contentDao;

    @Override
    public void handle()
        throws Exception
    {

        final Session jcrSession = context.getJcrSession();
        final Contents contents = contentDao.findChildContent( ContentPath.ROOT, jcrSession );
        command.setResult( contents );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
