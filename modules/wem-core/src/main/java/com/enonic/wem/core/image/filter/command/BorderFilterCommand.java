/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.effect.RectBorderFilter;

public final class BorderFilterCommand
    extends FilterCommand
{
    public BorderFilterCommand()
    {
        super( "border" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new RectBorderFilter( getIntArg( args, 0, 2 ), getIntArg( args, 1, 0x000000 ) );
    }
}
