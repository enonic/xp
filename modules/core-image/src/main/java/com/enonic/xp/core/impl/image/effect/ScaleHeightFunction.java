/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.filter.BaseImageProcessor;

public final class ScaleHeightFunction
    extends BaseImageProcessor implements ImageScaleFunction
{
    private final int size;

    public ScaleHeightFunction( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        float scale = (float) this.size / (float) height;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = this.size;

        return getScaledInstance( source, newWidth, newHeight );
    }
}
