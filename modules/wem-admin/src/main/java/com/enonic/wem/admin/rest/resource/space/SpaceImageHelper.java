package com.enonic.wem.admin.rest.resource.space;

import java.awt.image.BufferedImage;

import com.enonic.wem.admin.rest.resource.BaseImageHelper;

final class SpaceImageHelper
    extends BaseImageHelper
{
    private final BufferedImage defaultSpaceImage;

    public SpaceImageHelper()
        throws Exception
    {
        defaultSpaceImage = loadDefaultImage( "default_space" );
    }

    public BufferedImage getDefaultSpaceImage( final int size )
        throws Exception
    {
        return resizeImage( defaultSpaceImage, size );
    }

}
