/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class WideScale
    implements ImageFunction
{
    private final int width;

    private final int height;

    private final double offset;

    public WideScale( final int width, final int height, final double offset )
    {
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        final int newWidth = this.width;
        double scale = (double) newWidth / (double) sourceWidth;
        int newHeight = (int) ( scale * sourceHeight );

        int viewHeight = this.height;
        if ( this.height > newHeight )
        {
            viewHeight = newHeight;
        }

        int heightOffset = (int) ( newHeight * this.offset ) - ( viewHeight / 2 ); // center offset
        int heightDiff = newHeight - viewHeight;
        heightOffset = inRange( heightOffset, 0, heightDiff ); // adjust to view limits

        BufferedImage targetImage = ImageHelper.getScaledInstance( source, newWidth, newHeight );
        return targetImage.getSubimage( 0, heightOffset, newWidth, viewHeight );
    }

    private int inRange( final int value, final int min, final int max )
    {
        return Math.max( Math.min( value, max ), min );
    }
}
