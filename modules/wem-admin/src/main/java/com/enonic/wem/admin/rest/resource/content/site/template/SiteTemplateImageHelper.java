package com.enonic.wem.admin.rest.resource.content.site.template;

import java.awt.image.BufferedImage;

import com.enonic.wem.admin.rest.resource.BaseImageHelper;

final class SiteTemplateImageHelper
    extends BaseImageHelper
{
    private final BufferedImage defaultSiteTemplateImage;

    public SiteTemplateImageHelper()
    {
        defaultSiteTemplateImage = loadDefaultImage( "default-site-template" );
    }

    public BufferedImage getDefaultSiteTemplateImage( final int size )
    {
        return resizeImage( defaultSiteTemplateImage, size );
    }

}
