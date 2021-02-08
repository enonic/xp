package com.enonic.xp.core.impl.image;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;

public interface ImageScaleFunctionBuilder
{
    ImageFunction build( ScaleParams scaleParams, FocalPoint focalPoint );
}
