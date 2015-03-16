/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.GrayscaleFilter;

import com.enonic.xp.image.BuilderContext;

public final class GrayscaleFilterCommand
    extends FilterCommand
{
    public GrayscaleFilterCommand()
    {
        super( "grayscale" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new GrayscaleFilter();
    }
}