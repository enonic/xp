package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.NodeService;

public class AttachmentServiceImpl
    implements AttachmentService
{
    @Inject
    private NodeService nodeService;

    @Override
    public Attachment get( final ContentId contentId, final String attachmentName )
    {
        return new GetAttachmentCommand().
            contentId( contentId ).
            attachmentName( attachmentName ).
            nodeService( nodeService ).
            execute();
    }

    @Override
    public Attachments getAll( final ContentId contentId )
    {
        return new GetAttachmentsCommand().contentId( contentId ).nodeService( nodeService ).execute();
    }
}
