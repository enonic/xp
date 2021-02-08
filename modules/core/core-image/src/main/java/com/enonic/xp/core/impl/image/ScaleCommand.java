package com.enonic.xp.core.impl.image;

import com.enonic.xp.image.FocalPoint;

public interface ScaleCommand
{
    ImageFunction build( FocalPoint focalPoint, Object... args );
}
