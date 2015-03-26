package com.enonic.xp.image;

import com.enonic.xp.image.scale.ScaleParams;

public interface ImageScaleFunctionBuilder
{
    public ImageScaleFunction build( ScaleParams scaleParams );
}
