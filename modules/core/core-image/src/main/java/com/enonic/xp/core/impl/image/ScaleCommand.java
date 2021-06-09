package com.enonic.xp.core.impl.image;

import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.image.FocalPoint;

public interface ScaleCommand
{
    ImageScaleFunction build( FocalPoint focalPoint, Object... args );
}
