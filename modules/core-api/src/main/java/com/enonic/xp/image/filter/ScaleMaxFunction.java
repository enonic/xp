package com.enonic.xp.image.filter;

import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageScaleFunction;

import com.google.common.annotations.Beta;

@Beta
public final class ScaleMaxFunction
    extends BaseImageProcessor implements ImageScaleFunction
{
    private final int size;

    public ScaleMaxFunction( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int max = Math.max( width, height );

        float scale = (float) this.size / (float) max;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = (int) ( (float) height * scale );

        return getScaledInstance( source, newWidth, newHeight );
    }
}
