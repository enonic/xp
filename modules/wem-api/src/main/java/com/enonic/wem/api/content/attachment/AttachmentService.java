package com.enonic.wem.api.content.attachment;

public interface AttachmentService
{
    Attachment get( GetAttachmentParams params );

    Attachments getAll( GetAttachmentsParams params );
}
