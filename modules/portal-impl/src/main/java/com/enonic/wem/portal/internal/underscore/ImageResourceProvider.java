package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.image.ImageFilterBuilder;
import com.enonic.xp.web.jaxrs.ResourceProvider;

public final class ImageResourceProvider
    implements ResourceProvider<ImageResource>
{
    private ImageFilterBuilder imageFilterBuilder;

    private ContentService contentService;

    @Override
    public Class<ImageResource> getType()
    {
        return ImageResource.class;
    }

    @Override
    public ImageResource newResource()
    {
        final ImageResource instance = new ImageResource();
        instance.imageFilterBuilder = this.imageFilterBuilder;
        instance.contentService = this.contentService;
        return instance;
    }

    public final void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
