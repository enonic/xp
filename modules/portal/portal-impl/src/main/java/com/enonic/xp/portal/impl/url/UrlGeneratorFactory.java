package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.AttachmentMediaUrlParams;
import com.enonic.xp.portal.url.ImageMediaUrlParams;

public final class UrlGeneratorFactory
{
    private final ImageMediaUrlGenerator imageMediaUrlGenerator;

    private final AttachmentMediaUrlGenerator attachmentMediaUrlGenerator;

    public UrlGeneratorFactory( final ContentService contentService )
    {
        this.imageMediaUrlGenerator = new ImageMediaUrlGenerator( contentService );
        this.attachmentMediaUrlGenerator = new AttachmentMediaUrlGenerator( contentService );
    }

    public String generateImageMediaUrl( final ImageMediaUrlParams params )
    {
        return this.imageMediaUrlGenerator.generateUrl( params );
    }

    public String generateAttachmentMediaUrl( final AttachmentMediaUrlParams params )
    {
        return this.attachmentMediaUrlGenerator.generateUrl( params );
    }

}
