/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.FlipFilter;

import com.enonic.xp.image.BuilderContext;

public final class FlipVerticalCommand
    extends FilterCommand
{
    public FlipVerticalCommand()
    {
        super( "flipv" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new FlipFilter( FlipFilter.FLIP_V );
    }
}