package com.enonic.xp.image;

import com.enonic.xp.image.scale.ScaleParams;

public interface ImageScaleFunctionBuilder
{
    ImageScaleFunction build( ScaleParams scaleParams, FocalPoint focalPoint );
}
