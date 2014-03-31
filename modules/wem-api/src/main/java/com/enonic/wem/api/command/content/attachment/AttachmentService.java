package com.enonic.wem.api.command.content.attachment;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;

public interface AttachmentService
{
    Attachment get( GetAttachmentParams params );

    Attachments getAll( GetAttachmentsParams params );
}
