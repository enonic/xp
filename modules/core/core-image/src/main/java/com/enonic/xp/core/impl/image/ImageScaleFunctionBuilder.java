package com.enonic.xp.core.impl.image;

import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;

public interface ImageScaleFunctionBuilder
{
    ImageScaleFunction build( ScaleParams scaleParams, FocalPoint focalPoint );
}
