package com.enonic.xp.lib.portal.url;

import java.util.Objects;

import com.enonic.xp.image.ImageHelper;

public final class ImagePlaceholderHandler
{
    private Integer width;

    private Integer height;

    public String createImagePlaceholder()
    {
        return ImageHelper.createImagePlaceholder( Objects.requireNonNullElse( width, 0 ), Objects.requireNonNullElse( height, 0 ) );
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
