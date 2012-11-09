package com.enonic.wem.core.content;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContents;
import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;
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
        final ContentDeletionResult contentDeletionResult = new ContentDeletionResult();
        for ( ContentPath contentPath : command.getPaths() )
        {
            try
            {
                contentDao.deleteContent( contentPath, context.getJcrSession() );
                contentDeletionResult.success( contentPath );
            }
            catch ( ContentNotFoundException e )
            {
                contentDeletionResult.failure( contentPath, e );
            }
            catch ( UnableToDeleteContentException e )
            {
                contentDeletionResult.failure( contentPath, e );
            }
        }
        command.setResult( contentDeletionResult );
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
