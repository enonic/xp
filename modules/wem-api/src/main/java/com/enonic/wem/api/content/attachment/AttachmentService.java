package com.enonic.wem.api.content.attachment;

import com.enonic.wem.api.content.ContentId;

public interface AttachmentService
{
    Attachment get( final GetAttachmentParameters getAttachmentParameters );

    Attachments getAll( ContentId contentId );
}
