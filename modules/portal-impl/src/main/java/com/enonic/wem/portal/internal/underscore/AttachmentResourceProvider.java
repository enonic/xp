package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.content.ContentService;
import com.enonic.xp.web.jaxrs.ResourceProvider;

public final class AttachmentResourceProvider
    implements ResourceProvider<AttachmentResource>
{
    private ContentService contentService;

    @Override
    public Class<AttachmentResource> getType()
    {
        return AttachmentResource.class;
    }

    @Override
    public AttachmentResource newResource()
    {
        final AttachmentResource instance = new AttachmentResource();
        instance.contentService = this.contentService;
        return instance;
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
