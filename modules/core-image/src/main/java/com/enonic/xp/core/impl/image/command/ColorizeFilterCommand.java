/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.ColorizeFilter;

public final class ColorizeFilterCommand
    extends FilterCommand
{
    public ColorizeFilterCommand()
    {
        super( "colorize" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        float r = getFloatArg( args, 0, 1f );
        float g = getFloatArg( args, 1, 1f );
        float b = getFloatArg( args, 2, 1f );
        return new ColorizeFilter( r, g, b );
    }
}