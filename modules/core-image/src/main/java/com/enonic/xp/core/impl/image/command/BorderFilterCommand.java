/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.RectBorderFilter;

public final class BorderFilterCommand
    extends FilterCommand
{
    public BorderFilterCommand()
    {
        super( "border" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new RectBorderFilter( getIntArg( args, 0, 2 ), getIntArg( args, 1, 0x000000 ) );
    }
}
