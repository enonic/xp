package com.enonic.wem.portal.attachment;

import com.enonic.wem.portal.AbstractPortalService;

public class AttachmentServiceImpl
    extends AbstractPortalService
    implements AttachmentService
{
    @Override
    public String getAttachment( final AttachmentRequest attachmentRequest )
    {
        return attachmentRequest.toString();
    }
}
