package com.enonic.wem.portal.service;

import com.enonic.wem.portal.request.AttachmentRequest;

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
