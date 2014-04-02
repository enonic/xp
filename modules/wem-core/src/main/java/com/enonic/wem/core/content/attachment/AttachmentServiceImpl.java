package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.attachment.AttachmentService;
import com.enonic.wem.api.command.content.attachment.GetAttachmentParams;
import com.enonic.wem.api.command.content.attachment.GetAttachmentsParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.NodeService;

public class AttachmentServiceImpl
    implements AttachmentService
{
    @Inject
    private NodeService nodeService;

    @Override
    public Attachment get( final GetAttachmentParams params )
    {
        return new GetAttachmentCommand().params( params ).nodeService( nodeService ).execute();
    }

    @Override
    public Attachments getAll( final GetAttachmentsParams params )
    {
        return new GetAttachmentsCommand().params( params ).nodeService( nodeService ).execute();
    }
}
