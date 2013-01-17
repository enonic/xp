/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.jhlabs.image.GammaFilter;

import com.enonic.wem.core.image.filter.BuilderContext;

public final class GammaFilterCommand
    extends FilterCommand
{
    public GammaFilterCommand()
    {
        super( "gamma" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        double g = getDoubleArg( args, 0, 0.0 );
        return new GammaFilter( g );
    }
}