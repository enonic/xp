package com.enonic.wem.api.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;

public interface AttachmentService
{
    Attachment get( final GetAttachmentParameters getAttachmentParameters );

    Attachments getAll( ContentId contentId, Context context );
}
