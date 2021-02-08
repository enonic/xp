package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class MaxScale
    implements ImageFunction
{
    private final int size;

    public MaxScale( final int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int max = Math.max( width, height );

        float scale = (float) this.size / (float) max;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = (int) ( (float) height * scale );

        return ImageHelper.getScaledInstance( source, newWidth, newHeight );
    }
}
