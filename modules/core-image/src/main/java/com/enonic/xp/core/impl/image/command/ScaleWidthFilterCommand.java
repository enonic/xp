/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.image.BuilderContext;
import com.enonic.xp.core.image.filter.ScaleWidthFilter;

public final class ScaleWidthFilterCommand
    extends FilterCommand
{
    public ScaleWidthFilterCommand()
    {
        super( "scalewidth" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleWidthFilter( getIntArg( args, 0, 100 ) );
    }
}