package com.enonic.xp.portal.impl.handler.attachment;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.impl.handler.AbstractAttachmentHandlerWorker;
import com.enonic.xp.web.WebRequest;

public final class AttachmentHandlerWorker
    extends AbstractAttachmentHandlerWorker
{
    public AttachmentHandlerWorker( final WebRequest request, final ContentService contentService )
    {
        super( request, contentService );
    }
}
