package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.effect.BaseImageProcessor;

public final class ScaleFullFunction
    extends BaseImageProcessor
    implements ImageScaleFunction
{
    public ScaleFullFunction()
    {
    }

    @Override
    public BufferedImage scale( BufferedImage source )
    {
        return source;
    }
}
