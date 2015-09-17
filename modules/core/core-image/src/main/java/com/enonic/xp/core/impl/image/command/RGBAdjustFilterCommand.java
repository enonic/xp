/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.RGBAdjustFilter;

public final class RGBAdjustFilterCommand
    extends FilterCommand
{
    public RGBAdjustFilterCommand()
    {
        super( "rgbadjust" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        float r = getFloatArg( args, 0, 0f );
        float g = getFloatArg( args, 1, 0f );
        float b = getFloatArg( args, 2, 0f );

        return new RGBAdjustFilter( r, g, b );
    }
}