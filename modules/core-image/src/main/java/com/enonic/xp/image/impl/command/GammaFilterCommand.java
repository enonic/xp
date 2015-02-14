/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl.command;

import com.jhlabs.image.GammaFilter;

import com.enonic.wem.api.image.BuilderContext;

public final class GammaFilterCommand
    extends FilterCommand
{
    public GammaFilterCommand()
    {
        super( "gamma" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        float g = getFloatArg( args, 0, 0f );
        return new GammaFilter( g );
    }
}