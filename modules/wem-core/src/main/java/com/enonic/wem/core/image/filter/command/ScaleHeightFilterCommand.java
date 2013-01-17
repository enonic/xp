/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.effect.ScaleHeightFilter;

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
