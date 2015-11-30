package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import com.google.common.annotations.Beta;

import com.enonic.xp.core.impl.image.effect.BaseImageProcessor;

@Beta
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
