/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.ScaleWideFunction;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageScaleFunction;

public final class ScaleWideFunctionCommand
    extends ScaleCommand
{
    public ScaleWideFunctionCommand()
    {
        super( "wide" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint )
    {
        return new ScaleWideFunction( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getDoubleArg( args, 2, focalPoint.yOffset() ) );
    }
}
