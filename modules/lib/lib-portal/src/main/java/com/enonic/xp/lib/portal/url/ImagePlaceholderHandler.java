package com.enonic.xp.lib.portal.url;

import com.enonic.xp.image.ImageHelper;

public final class ImagePlaceholderHandler
{
    private Integer width;

    private Integer height;

    public String createImagePlaceholder()
    {
        width = width != null ? width : 0;
        height = height != null ? height : 0;
        return ImageHelper.createImagePlaceholder( width, height );
    }

    public void setWidth( final Integer width )
    {
        this.width = width;
    }

    public void setHeight( final Integer height )
    {
        this.height = height;
    }
}
