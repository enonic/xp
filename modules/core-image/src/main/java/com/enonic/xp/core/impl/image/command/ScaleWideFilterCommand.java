/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.BuilderContext;
import com.enonic.xp.core.impl.image.effect.ScaleWideFilter;

public final class ScaleWideFilterCommand
    extends FilterCommand
{
    public ScaleWideFilterCommand()
    {
        super( "scalewide" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleWideFilter( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getFloatArg( args, 2, 0.5f ) );
    }
}
