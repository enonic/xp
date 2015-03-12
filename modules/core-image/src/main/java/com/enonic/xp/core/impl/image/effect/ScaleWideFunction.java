/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.filter.BaseImageProcessor;

public final class ScaleWideFunction
    extends BaseImageProcessor implements ImageScaleFunction
{
    private final int width;

    private final int height;

    private final float offset;

    public ScaleWideFunction( int width, int height, float offset )
    {
        this.width = width;
        this.height = height;
        this.offset = Math.max( Math.min( offset, 1f ), 0 );
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float scale = (float) this.width / (float) sourceWidth;
        int newHeight = (int) ( scale * sourceHeight );

        int viewHeight = this.height;
        if ( this.height > newHeight )
        {
            viewHeight = newHeight;
        }

        int heightDiff = newHeight - viewHeight;
        int heightOffset = (int) ( heightDiff * this.offset );

        BufferedImage targetImage = getScaledInstance( source, this.width, newHeight );
        return targetImage.getSubimage( 0, heightOffset, this.width, viewHeight );
    }
}
