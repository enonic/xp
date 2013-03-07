package com.enonic.wem.core.content;


import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.index.IndexService;

@Component
public class DeleteContentHandler
    extends CommandHandler<DeleteContent>
{
    private ContentDao contentDao;

    private IndexService indexService;

    public DeleteContentHandler()
    {
        super( DeleteContent.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteContent command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        // Temporary solution to ease the index-service since content selector are supposed to be rewritten
        final Content contentToDelete = contentDao.select( command.getSelector(), session );

        if ( contentToDelete == null )
        {
            command.setResult( DeleteContentResult.NOT_FOUND );
            return;
        }

        try
        {
            contentDao.delete( command.getSelector(), session );
            session.save();
            indexService.deleteContent( contentToDelete.getId() );
            command.setResult( DeleteContentResult.SUCCESS );
        }
        catch ( ContentNotFoundException | UnableToDeleteContentException e )
        {
            command.setResult( DeleteContentResult.from( e ) );
        }
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
