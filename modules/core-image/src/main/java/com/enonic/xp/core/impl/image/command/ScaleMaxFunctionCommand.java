/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.filter.ScaleMaxFunction;

public final class ScaleMaxFunctionCommand
    extends ScaleCommand
{
    public ScaleMaxFunctionCommand()
    {
        super( "max" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args, FocalPoint focalPoint )
    {
        return new ScaleMaxFunction( getIntArg( args, 0, 0 ) );
    }
}