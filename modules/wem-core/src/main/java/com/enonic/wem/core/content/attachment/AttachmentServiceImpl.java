package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.attachment.GetAttachmentParameters;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.entity.NodeService;

public class AttachmentServiceImpl
    implements AttachmentService
{
    private NodeService nodeService;

    @Override
    public Attachment get( final GetAttachmentParameters getAttachmentParameters )
    {
        return GetAttachmentCommand.create().
            contentId( getAttachmentParameters.getContentId() ).
            attachmentName( getAttachmentParameters.getAttachmentName() ).
            nodeService( nodeService ).
            context( getAttachmentParameters.getContext() ).
            build().
            execute();
    }

    @Override
    public Attachments getAll( final ContentId contentId, final Context context )
    {
        return GetAttachmentsCommand.create().
            contentId( contentId ).
            nodeService( nodeService ).
            context( context ).
            build().
            execute();
    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
