/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.ScaleHeightFunction;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageScaleFunction;

public final class ScaleHeightFunctionCommand
    extends ScaleCommand
{
    public ScaleHeightFunctionCommand()
    {
        super( "height" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint )
    {
        return new ScaleHeightFunction( getIntArg( args, 0, 100 ) );
    }
}
