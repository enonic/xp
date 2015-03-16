/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.BuilderContext;
import com.enonic.xp.core.impl.image.effect.ScaleHeightFilter;

public final class ScaleHeightFilterCommand
    extends FilterCommand
{
    public ScaleHeightFilterCommand()
    {
        super( "scaleheight" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleHeightFilter( getIntArg( args, 0, 100 ) );
    }
}
