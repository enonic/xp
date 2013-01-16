/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.image.BufferedImage;

public final class ScaleSquareFilter
    extends BaseImageFilter
{
    private final int size;

    public ScaleSquareFilter( int size )
    {
        this.size = size;
    }

    public BufferedImage filter( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage cropped;
        if ( width < height )
        {
            int offset = (int) ( ( height - width ) / 2f );
            cropped = source.getSubimage( 0, offset, width, width );
        }
        else
        {
            int offset = (int) ( ( width - height ) / 2f );
            cropped = source.getSubimage( offset, 0, height, height );
        }

        return getScaledInstance( cropped, this.size, this.size );
    }
}
