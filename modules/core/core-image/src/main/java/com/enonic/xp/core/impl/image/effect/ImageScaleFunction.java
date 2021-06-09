package com.enonic.xp.core.impl.image.effect;

import com.enonic.xp.core.impl.image.ImageFunction;

public interface ImageScaleFunction
    extends ImageFunction
{
    int estimateResolution( int sourceWidth, int sourceHeight );
}
