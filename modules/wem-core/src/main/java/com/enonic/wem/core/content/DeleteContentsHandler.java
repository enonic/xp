package com.enonic.wem.core.content;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContents;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class DeleteContentsHandler
    extends CommandHandler<DeleteContents>
{
    private ContentDao contentDao;

    public DeleteContentsHandler()
    {
        super( DeleteContents.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteContents command )
        throws Exception
    {
        for ( ContentPath contentPath : command.getPaths() )
        {
            contentDao.deleteContent( contentPath, context.getJcrSession() );
        }
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
