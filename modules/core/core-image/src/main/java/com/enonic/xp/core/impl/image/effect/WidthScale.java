package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class WidthScale
    implements ImageFunction
{
    private final int size;

    public WidthScale( final int size )
    {
        this.size = size;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        float scale = (float) this.size / (float) width;
        int newWidth = this.size;
        int newHeight = (int) ( (float) height * scale );

        return ImageHelper.getScaledInstance( source, newWidth, newHeight );
    }
}
