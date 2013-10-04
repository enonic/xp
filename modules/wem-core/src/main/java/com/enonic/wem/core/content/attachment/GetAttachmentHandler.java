package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;


public class GetAttachmentHandler
    extends CommandHandler<GetAttachment>
{
    private AttachmentDao attachmentDao;

    @Override
    public void handle( final CommandContext context, final GetAttachment command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final Attachment attachment = attachmentDao.getAttachment( command.getContentSelector(), command.getAttachmentName(), session );
        session.save();
        command.setResult( attachment );
    }

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
