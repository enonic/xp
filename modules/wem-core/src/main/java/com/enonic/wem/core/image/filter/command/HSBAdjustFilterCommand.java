/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.jhlabs.image.HSBAdjustFilter;

import com.enonic.wem.core.image.filter.BuilderContext;

public final class HSBAdjustFilterCommand
    extends FilterCommand
{
    public HSBAdjustFilterCommand()
    {
        super( "hsbadjust" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        double h = getDoubleArg( args, 0, 0.0 );
        double s = getDoubleArg( args, 1, 0.0 );
        double b = getDoubleArg( args, 2, 0.0 );

        return new HSBAdjustFilter( h, s, b );
    }
}