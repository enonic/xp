/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.HSBAdjustFilter;

import com.enonic.xp.image.BuilderContext;

public final class HSBAdjustFilterCommand
    extends FilterCommand
{
    public HSBAdjustFilterCommand()
    {
        super( "hsbadjust" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        float h = getFloatArg( args, 0, 0f );
        float s = getFloatArg( args, 1, 0f );
        float b = getFloatArg( args, 2, 0f );

        return new HSBAdjustFilter( h, s, b );
    }
}