/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl.command;

import com.enonic.wem.api.image.BuilderContext;
import com.enonic.xp.image.impl.effect.ScaleHeightFilter;

public final class ScaleHeightFilterCommand
    extends FilterCommand
{
    public ScaleHeightFilterCommand()
    {
        super( "scaleheight" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleHeightFilter( getIntArg( args, 0, 100 ) );
    }
}
