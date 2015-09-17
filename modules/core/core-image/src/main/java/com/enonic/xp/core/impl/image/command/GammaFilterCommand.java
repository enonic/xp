/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.GammaFilter;

public final class GammaFilterCommand
    extends FilterCommand
{
    public GammaFilterCommand()
    {
        super( "gamma" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        float g = getFloatArg( args, 0, 0f );
        return new GammaFilter( g );
    }
}