/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.filter.ScaleWidthFunction;

public final class ScaleWidthFunctionCommand
    extends ScaleCommand
{
    public ScaleWidthFunctionCommand()
    {
        super( "width" );
    }

    @Override
    protected ImageScaleFunction doBuild( Object[] args )
    {
        return new ScaleWidthFunction( getIntArg( args, 0, 100 ) );
    }
}