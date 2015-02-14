/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl.command;

import com.jhlabs.image.SharpenFilter;

import com.enonic.wem.api.image.BuilderContext;

public final class SharpenFilterCommand
    extends FilterCommand
{
    public SharpenFilterCommand()
    {
        super( "sharpen" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new SharpenFilter();
    }
}