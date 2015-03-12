package com.enonic.xp.image.filter;

import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageScaleFunction;

public final class ScaleSquareFunction
    extends BaseImageProcessor implements ImageScaleFunction
{
    private final int size;

    public ScaleSquareFunction( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage scale( BufferedImage source )
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
