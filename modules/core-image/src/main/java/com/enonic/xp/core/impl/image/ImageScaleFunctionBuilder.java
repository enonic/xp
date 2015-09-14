package com.enonic.xp.core.impl.image;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.scale.ScaleParams;

public interface ImageScaleFunctionBuilder
{
    ImageScaleFunction build( ScaleParams scaleParams, FocalPoint focalPoint );
}
