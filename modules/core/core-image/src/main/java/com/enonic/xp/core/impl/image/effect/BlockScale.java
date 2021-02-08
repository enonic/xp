package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class BlockScale
    implements ImageFunction
{
    private final int width;

    private final int height;

    private final double xOffset;

    private final double yOffset;

    public BlockScale( int width, int height, double xOffset, double yOffset )
    {
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        double ratio = (double) sourceWidth / (double) sourceHeight;

        int newWidth = this.width;
        int newHeight = this.height;

        final double scale;
        if ( ratio > 1 )
        {
            scale = ratio;
            newHeight = (int) ( newWidth / scale );
        }
        else
        {
            scale = 1 / ratio;
            newWidth = (int) ( newHeight / scale );
        }

        int viewWidth = this.width;
        if ( newWidth < this.width )
        {
            newWidth = this.width;
            newHeight = (int) ( newWidth * scale );
            viewWidth = newWidth;
        }

        int viewHeight = this.height;
        if ( newHeight < this.height )
        {
            newHeight = this.height;
            newWidth = (int) ( newHeight * scale );
            viewHeight = newHeight;
        }

        int widthDiff = newWidth - viewWidth;
        int widthOffset = (int) ( newWidth * this.xOffset ) - ( viewWidth / 2 ); // center xOffset
        widthOffset = inRange( widthOffset, 0, widthDiff ); // adjust to view limits

        int heightDiff = newHeight - viewHeight;
        int heightOffset = (int) ( newHeight * this.yOffset ) - ( viewHeight / 2 ); // center yOffset
        heightOffset = inRange( heightOffset, 0, heightDiff ); // adjust to view limits

        BufferedImage targetImage = ImageHelper.getScaledInstance( source, newWidth, newHeight );
        return targetImage.getSubimage( widthOffset, heightOffset, viewWidth, viewHeight );
    }

    private int inRange( final int value, final int min, final int max )
    {
        return Math.max( Math.min( value, max ), min );
    }
}
