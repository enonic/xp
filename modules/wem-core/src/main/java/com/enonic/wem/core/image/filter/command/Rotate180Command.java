/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.jhlabs.image.FlipFilter;

import com.enonic.wem.core.image.filter.BuilderContext;

public final class Rotate180Command
    extends FilterCommand
{
    public Rotate180Command()
    {
        super( "rotate180" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new FlipFilter( FlipFilter.FLIP_180 );
    }
}