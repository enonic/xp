package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;

public final class FullScale
    implements ImageFunction
{
    @Override
    public BufferedImage apply( BufferedImage source )
    {
        return source;
    }
}
