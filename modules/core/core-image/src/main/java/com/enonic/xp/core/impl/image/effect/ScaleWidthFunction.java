package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageScaleFunction;

public final class ScaleWidthFunction
    extends BaseImageProcessor implements ImageScaleFunction
{
    private final int size;

    public ScaleWidthFunction( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        float scale = (float) this.size / (float) width;
        int newWidth = this.size;
        int newHeight = (int) ( (float) height * scale );

        return getScaledInstance( source, newWidth, newHeight );
    }
}
