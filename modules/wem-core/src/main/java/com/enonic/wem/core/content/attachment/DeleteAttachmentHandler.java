package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.DeleteAttachment;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;


public class DeleteAttachmentHandler
    extends CommandHandler<DeleteAttachment>
{
    private AttachmentDao attachmentDao;

    @Override
    public void handle( final CommandContext context, final DeleteAttachment command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final boolean deleted = attachmentDao.deleteAttachment( command.getContentSelector(), command.getAttachmentName(), session );
        session.save();
        command.setResult( deleted );
    }

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
