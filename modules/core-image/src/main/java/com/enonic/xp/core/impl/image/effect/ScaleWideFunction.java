/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageScaleFunction;
import com.enonic.xp.image.filter.BaseImageProcessor;

public final class ScaleWideFunction
    extends BaseImageProcessor
    implements ImageScaleFunction
{
    private final int width;

    private final int height;

    private final double offset;

    public ScaleWideFunction( int width, int height, double offset )
    {
        this.width = width;
        this.height = height;
        this.offset = Math.max( Math.min( offset, 1 ), 0 );
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        double scale = (double) this.width / (double) sourceWidth;
        int newHeight = (int) ( scale * sourceHeight );

        int viewHeight = this.height;
        if ( this.height > newHeight )
        {
            viewHeight = newHeight;
        }

        int heightOffset = (int) ( newHeight * this.offset ) - ( viewHeight / 2 ); // center offset
        int heightDiff = newHeight - viewHeight;
        heightOffset = inRange( heightOffset, 0, heightDiff ); // adjust to view limits

        BufferedImage targetImage = getScaledInstance( source, this.width, newHeight );
        return targetImage.getSubimage( 0, heightOffset, this.width, viewHeight );
    }

    private int inRange( final int value, final int min, final int max )
    {
        return Math.max( Math.min( value, max ), min );
    }
}
