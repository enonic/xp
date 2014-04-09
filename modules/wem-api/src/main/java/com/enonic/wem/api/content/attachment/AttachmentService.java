package com.enonic.wem.api.content.attachment;

import com.enonic.wem.api.content.ContentId;

public interface AttachmentService
{
    Attachment get( ContentId contentId, String attachmentName );

    Attachments getAll( ContentId contentId );
}
