package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class SquareScale
    implements ImageFunction
{
    private final int size;

    private final double xOffset;

    private final double yOffset;

    public SquareScale( final int size, final double xOffset, final double yOffset )
    {
        this.size = size;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public BufferedImage apply( final BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();

        final int smallSize;
        final int bigSize;
        final double offsetBase;
        final int x;
        final int y;
        if ( width < height )
        {
            smallSize = width;
            bigSize = height;
            offsetBase = yOffset;
            x = 0;
            y = centerOffset( bigSize, smallSize, offsetBase );
        }
        else
        {
            smallSize = height;
            bigSize = width;
            offsetBase = xOffset;
            x = centerOffset( bigSize, smallSize, offsetBase );
            y = 0;
        }
        final BufferedImage cropped = source.getSubimage( x, y, smallSize, smallSize );

        return ImageHelper.getScaledInstance( cropped, size, size );
    }

    private int centerOffset( final int value1, final int value2, final double offset )
    {
        int diff = value1 - value2;
        final int centered = (int) ( value1 * offset ) - ( value2 / 2 );
        return Math.max( Math.min( centered, diff ), 0 );
    }
}
