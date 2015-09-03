package com.enonic.xp.image.filter;

import java.awt.image.BufferedImage;

import com.google.common.annotations.Beta;

import com.enonic.xp.image.ImageScaleFunction;

@Beta
public final class ScaleSquareFunction
    extends BaseImageProcessor
    implements ImageScaleFunction
{
    private final int size;

    private final double xOffset;

    private final double yOffset;

    public ScaleSquareFunction( int size, double xOffset, double yOffset )
    {
        this.size = size;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public ScaleSquareFunction( int size )
    {
        this( size, 0.5, 0.5 );
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage cropped;
        if ( width < height )
        {
            int heightDiff = height - width;
            int offset = (int) ( height * this.yOffset ) - ( width / 2 ); // center offset
            offset = inRange( offset, 0, heightDiff ); // adjust to view limits

            cropped = source.getSubimage( 0, offset, width, width );
        }
        else
        {
            int widthDiff = width - height;
            int offset = (int) ( width * this.xOffset ) - ( height / 2 ); // center offset
            offset = inRange( offset, 0, widthDiff ); // adjust to view limits

            cropped = source.getSubimage( offset, 0, height, height );
        }

        return getScaledInstance( cropped, this.size, this.size );
    }

    private int inRange( final int value, final int min, final int max )
    {
        return Math.max( Math.min( value, max ), min );
    }
}
