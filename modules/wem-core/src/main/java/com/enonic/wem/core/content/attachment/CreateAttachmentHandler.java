package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.CreateAttachment;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;


public class CreateAttachmentHandler
    extends CommandHandler<CreateAttachment>
{
    private AttachmentDao attachmentDao;

    @Override
    public void handle( final CommandContext context, final CreateAttachment command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        attachmentDao.createAttachment( command.getContentSelector(), command.getAttachment(), session );
        session.save();
    }

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
