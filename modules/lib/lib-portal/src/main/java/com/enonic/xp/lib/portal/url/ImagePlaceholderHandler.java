package com.enonic.xp.lib.portal.url;

import com.enonic.xp.image.ImageHelper;

import static java.util.Objects.requireNonNullElse;

public final class ImagePlaceholderHandler
{
    private Integer width;

    private Integer height;

    public String createImagePlaceholder()
    {
        return ImageHelper.createImagePlaceholder( requireNonNullElse( width, 0 ), requireNonNullElse( height, 0 ) );
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
