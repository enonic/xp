package com.enonic.xp.image.filter;

import java.awt.image.BufferedImage;

import com.google.common.annotations.Beta;

@Beta
public final class ScaleMaxFilter
    extends BaseImageFilter
{
    private final int size;

    public ScaleMaxFilter( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage filter( BufferedImage source )
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
