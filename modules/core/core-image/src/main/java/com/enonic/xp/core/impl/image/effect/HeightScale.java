/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class HeightScale
    implements ImageFunction
{
    private final int size;

    public HeightScale( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        float scale = (float) this.size / (float) height;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = this.size;

        return ImageHelper.getScaledInstance( source, newWidth, newHeight );
    }
}
