package com.enonic.xp.image.filter;

import java.awt.image.BufferedImage;

public final class ScaleWidthFilter
    extends BaseImageFilter
{
    private final int size;

    public ScaleWidthFilter( int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage filter( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        float scale = (float) this.size / (float) width;
        int newWidth = this.size;
        int newHeight = (int) ( (float) height * scale );

        return getScaledInstance( source, newWidth, newHeight );
    }
}
