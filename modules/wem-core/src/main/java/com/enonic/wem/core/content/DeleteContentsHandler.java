package com.enonic.wem.core.content;


import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContents;
import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
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
        final Iterable<ContentSelector> selectors = command.getSelectors();
        for ( ContentSelector contentSelector : selectors )
        {
            try
            {
                deleteContent( contentSelector, context.getJcrSession() );
                contentDeletionResult.success( contentSelector );
                context.getJcrSession().save();
            }
            catch ( ContentNotFoundException e )
            {
                contentDeletionResult.failure( contentSelector, e );
            }
            catch ( UnableToDeleteContentException e )
            {
                contentDeletionResult.failure( contentSelector, e );
            }
        }
        command.setResult( contentDeletionResult );
    }

    private void deleteContent( final ContentSelector contentSelector, final Session session )
    {
        if ( contentSelector instanceof ContentPath )
        {
            contentDao.deleteContent( (ContentPath) contentSelector, session );
        }
        else if ( contentSelector instanceof ContentId )
        {
//            contentDao.deleteContent( (ContentId) contentSelector, session );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported content selector: " + contentSelector.getClass().getCanonicalName() );
        }
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
