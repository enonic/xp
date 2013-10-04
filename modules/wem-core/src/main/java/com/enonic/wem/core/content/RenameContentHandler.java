package com.enonic.wem.core.content;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.RenameContentException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;
import com.enonic.wem.core.content.dao.ContentDao;


public class RenameContentHandler
    extends CommandHandler<RenameContent>
{
    private ContentDao contentDao;

    private AttachmentDao attachmentDao;

    @Override
    public void handle( final CommandContext context, final RenameContent command )
        throws Exception
    {
        try
        {
            final Session session = context.getJcrSession();
            final ContentId contentId = command.getContentId();
            final Content content = contentDao.select( contentId, session );
            if ( content == null )
            {
                throw new ContentNotFoundException( contentId );
            }

            final String oldName = content.getName();
            final String newName = command.getNewName();

            final boolean renamed = contentDao.renameContent( contentId, newName, session );
            if ( renamed )
            {
                renameAttachments( contentId, oldName, newName, session );
            }
            command.setResult( renamed );
            session.save();
        }
        catch ( ContentNotFoundException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new RenameContentException( command, e );
        }
    }

    private void renameAttachments( final ContentId contentId, final String oldName, final String newName, final Session session )
    {
        attachmentDao.renameAttachments( contentId, oldName, newName, session );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
