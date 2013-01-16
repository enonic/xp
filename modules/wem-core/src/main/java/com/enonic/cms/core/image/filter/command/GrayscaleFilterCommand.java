/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.command;

import com.jhlabs.image.GrayscaleFilter;

import com.enonic.cms.core.image.filter.BuilderContext;

public final class GrayscaleFilterCommand
    extends FilterCommand
{
    public GrayscaleFilterCommand()
    {
        super( "grayscale" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new GrayscaleFilter();
    }
}